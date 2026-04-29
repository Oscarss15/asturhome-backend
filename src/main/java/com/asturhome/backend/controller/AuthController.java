package com.asturhome.backend.controller;

import com.asturhome.backend.dto.CambiarPasswordRequest;
import com.asturhome.backend.dto.ForgotPasswordRequest;
import com.asturhome.backend.dto.LoginRequest;
import com.asturhome.backend.dto.LoginResponse;
import com.asturhome.backend.dto.ResetPasswordRequest;
import com.asturhome.backend.entity.PasswordResetToken;
import com.asturhome.backend.entity.User;
import com.asturhome.backend.repository.PasswordResetTokenRepository;
import com.asturhome.backend.repository.UserRepository;
import com.asturhome.backend.security.JwtUtil;
import com.asturhome.backend.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository resetTokenRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            User user = userRepository.findByEmail(request.email()).orElseThrow();
            String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

            return ResponseEntity.ok(new LoginResponse(token, user.getRole().name(), user.getEmail(), user.getName(), user.getAvatarUrl(), user.getPreferencias()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body(Map.of("error", "Correo o contraseña incorrectos"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest req) {
        userRepository.findByEmail(req.email()).ifPresent(user -> {
            resetTokenRepository.deleteByUser(user);
            String token = UUID.randomUUID().toString();
            resetTokenRepository.save(new PasswordResetToken(token, user, LocalDateTime.now().plusMinutes(30)));
            String enlace = frontendUrl + "/reset-password?token=" + token;
            emailService.enviarResetPassword(user.getEmail(), enlace);
        });
        return ResponseEntity.ok(Map.of("mensaje", "Si el correo existe, recibirás un enlace en breve"));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        PasswordResetToken resetToken = resetTokenRepository.findByToken(req.token()).orElse(null);
        if (resetToken == null || resetToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(400).body(Map.of("error", "El enlace no es válido o ha expirado"));
        }
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(req.newPassword()));
        userRepository.save(user);
        resetTokenRepository.delete(resetToken);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña restablecida correctamente"));
    }

    @PutMapping("/password")
    public ResponseEntity<?> cambiarPassword(@RequestBody CambiarPasswordRequest req) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!passwordEncoder.matches(req.passwordActual(), user.getPassword())) {
            return ResponseEntity.status(400).body(Map.of("error", "La contraseña actual no es correcta"));
        }

        user.setPassword(passwordEncoder.encode(req.passwordNueva()));
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("mensaje", "Contraseña actualizada correctamente"));
    }
}
