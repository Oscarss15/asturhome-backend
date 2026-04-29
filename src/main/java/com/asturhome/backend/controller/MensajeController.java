package com.asturhome.backend.controller;

import com.asturhome.backend.dto.MensajePublicoRequest;
import com.asturhome.backend.dto.MensajeResponse;
import com.asturhome.backend.dto.RespuestaAgenteRequest;
import com.asturhome.backend.dto.RespuestaClienteRequest;
import com.asturhome.backend.service.MensajeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/mensajes")
public class MensajeController {

    @Autowired
    private MensajeService mensajeService;

    // Público: cliente envía consulta desde la web
    @PostMapping("/publico")
    public ResponseEntity<MensajeResponse> crearPublico(@RequestBody MensajePublicoRequest req) {
        return ResponseEntity.ok(mensajeService.crearMensajePublico(req));
    }

    // Público: obtener datos del hilo para la página de respuesta del cliente
    @GetMapping("/responder/{token}")
    public ResponseEntity<MensajeResponse> obtenerPorToken(@PathVariable String token) {
        return ResponseEntity.ok(mensajeService.obtenerPorToken(token));
    }

    // Público: cliente responde desde su enlace
    @PostMapping("/responder/{token}")
    public ResponseEntity<Void> responderCliente(@PathVariable String token,
                                                  @RequestBody RespuestaClienteRequest req) {
        mensajeService.responderCliente(token, req);
        return ResponseEntity.ok().build();
    }

    // Protegido: listar todos los mensajes
    @GetMapping
    public ResponseEntity<List<MensajeResponse>> listar() {
        return ResponseEntity.ok(mensajeService.listarTodos());
    }

    // Protegido: obtener un mensaje
    @GetMapping("/{id}")
    public ResponseEntity<MensajeResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(mensajeService.obtenerPorId(id));
    }

    // Protegido: cambiar estado
    @PutMapping("/{id}/estado")
    public ResponseEntity<MensajeResponse> cambiarEstado(@PathVariable Long id,
                                                          @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(mensajeService.cambiarEstado(id, body.get("estado")));
    }

    // Protegido: agente responde o añade nota
    @PostMapping("/{id}/respuestas")
    public ResponseEntity<MensajeResponse> responderAgente(@PathVariable Long id,
                                                            @RequestBody RespuestaAgenteRequest req) {
        return ResponseEntity.ok(mensajeService.responderAgente(id, req));
    }
}
