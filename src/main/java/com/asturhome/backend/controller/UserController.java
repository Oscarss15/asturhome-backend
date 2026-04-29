package com.asturhome.backend.controller;

import com.asturhome.backend.entity.Role;
import com.asturhome.backend.entity.User;
import com.asturhome.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

record PreferenciasRequest(String preferencias) {}
record AgenteCreateRequest(String nombre, String email, String password) {}
record AgenteEditRequest(String nombre, String email) {}

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${upload.dir:uploads}")
    private String uploadDir;

    @Value("${upload.base-url:http://localhost:8080/uploads}")
    private String uploadBaseUrl;

    @PutMapping("/preferencias")
    public ResponseEntity<?> guardarPreferencias(@RequestBody PreferenciasRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();
        user.setPreferencias(req.preferencias());
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @GetMapping("/agentes")
    public ResponseEntity<?> listarAgentes() {
        List<Map<String, Object>> agentes = userRepository.findAll().stream()
            .filter(u -> u.getRole() == Role.AGENT)
            .map(u -> {
                Map<String, Object> m = new HashMap<>();
                m.put("id",        u.getId());
                m.put("nombre",    u.getName());
                m.put("email",     u.getEmail());
                m.put("avatarUrl", u.getAvatarUrl() != null ? u.getAvatarUrl() : "");
                return m;
            })
            .collect(Collectors.toList());
        return ResponseEntity.ok(agentes);
    }

    @PostMapping("/agentes")
    public ResponseEntity<?> crearAgente(@RequestBody AgenteCreateRequest req) {
        if (userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "El email ya está en uso"));
        }
        User user = new User();
        user.setName(req.nombre());
        user.setEmail(req.email());
        user.setPassword(passwordEncoder.encode(req.password()));
        user.setRole(Role.AGENT);
        User saved = userRepository.save(user);
        Map<String, Object> res = new HashMap<>();
        res.put("id",        saved.getId());
        res.put("nombre",    saved.getName());
        res.put("email",     saved.getEmail());
        res.put("avatarUrl", "");
        return ResponseEntity.ok(res);
    }

    @PutMapping("/agentes/{id}")
    public ResponseEntity<?> editarAgente(@PathVariable Long id, @RequestBody AgenteEditRequest req) {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getRole() != Role.AGENT) {
            return ResponseEntity.status(403).body(Map.of("error", "No permitido"));
        }
        if (!user.getEmail().equals(req.email()) && userRepository.findByEmail(req.email()).isPresent()) {
            return ResponseEntity.status(409).body(Map.of("error", "El email ya está en uso"));
        }
        user.setName(req.nombre());
        user.setEmail(req.email());
        userRepository.save(user);
        Map<String, Object> res = new HashMap<>();
        res.put("id",        user.getId());
        res.put("nombre",    user.getName());
        res.put("email",     user.getEmail());
        res.put("avatarUrl", user.getAvatarUrl() != null ? user.getAvatarUrl() : "");
        return ResponseEntity.ok(res);
    }

    @DeleteMapping("/agentes/{id}")
    public ResponseEntity<?> eliminarAgente(@PathVariable Long id) {
        User user = userRepository.findById(id).orElseThrow();
        if (user.getRole() != Role.AGENT) {
            return ResponseEntity.status(403).body(Map.of("error", "No se puede eliminar un administrador"));
        }
        userRepository.delete(user);
        return ResponseEntity.ok(Map.of("ok", true));
    }

    @PostMapping("/avatar")
    public ResponseEntity<?> subirAvatar(@RequestParam("file") MultipartFile file) throws IOException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        String ext = file.getOriginalFilename() != null && file.getOriginalFilename().contains(".")
            ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".")) : ".jpg";
        String filename = "avatar-" + user.getId() + "-" + UUID.randomUUID() + ext;
        Path dir = Paths.get(uploadDir);
        Files.createDirectories(dir);
        Files.copy(file.getInputStream(), dir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

        String url = uploadBaseUrl + "/" + filename;
        user.setAvatarUrl(url);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("avatarUrl", url));
    }
}
