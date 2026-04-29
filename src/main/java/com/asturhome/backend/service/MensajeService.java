package com.asturhome.backend.service;

import com.asturhome.backend.dto.MensajePublicoRequest;
import com.asturhome.backend.dto.MensajeResponse;
import com.asturhome.backend.dto.RespuestaAgenteRequest;
import com.asturhome.backend.dto.RespuestaClienteRequest;
import com.asturhome.backend.entity.Mensaje;
import com.asturhome.backend.entity.RespuestaMensaje;
import com.asturhome.backend.repository.MensajeRepository;
import com.asturhome.backend.repository.RespuestaMensajeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MensajeService {

    @Autowired
    private MensajeRepository mensajeRepository;

    @Autowired
    private RespuestaMensajeRepository respuestaRepository;

    @Autowired
    private EmailService emailService;

    @Value("${app.agente-email}")
    private String emailAgente;

    // Cliente envía mensaje desde la web pública
    @Transactional
    public MensajeResponse crearMensajePublico(MensajePublicoRequest req) {
        Mensaje m = new Mensaje();
        m.setNombre(req.getNombre());
        m.setEmail(req.getEmail());
        m.setTelefono(req.getTelefono());
        m.setTipoConsulta(req.getTipoConsulta());
        m.setPropiedad(req.getPropiedad());
        m.setModalidad(req.getModalidad());
        m.setMensajeOriginal(req.getMensaje());
        m.setEstado("nuevos");
        m.setFecha(LocalDateTime.now());
        m.setTokenRespuesta(UUID.randomUUID().toString());

        // Primer mensaje del hilo
        RespuestaMensaje primera = new RespuestaMensaje();
        primera.setMensaje(m);
        primera.setTipo("cliente");
        primera.setTexto(req.getMensaje());
        primera.setFecha(LocalDateTime.now());
        m.getHilo().add(primera);

        Mensaje saved = mensajeRepository.save(m);

        // Notificar al agente por email
        emailService.notificarNuevoMensaje(emailAgente, req.getNombre(), req.getMensaje());

        return MensajeResponse.from(saved);
    }

    // Listar todos los mensajes
    @Transactional(readOnly = true)
    public List<MensajeResponse> listarTodos() {
        return mensajeRepository.findAll()
                .stream()
                .map(MensajeResponse::from)
                .collect(Collectors.toList());
    }

    // Obtener un mensaje por ID
    @Transactional(readOnly = true)
    public MensajeResponse obtenerPorId(Long id) {
        Mensaje m = mensajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        return MensajeResponse.from(m);
    }

    // Cambiar estado (drag & drop o dropdown)
    @Transactional
    public MensajeResponse cambiarEstado(Long id, String nuevoEstado) {
        Mensaje m = mensajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));
        m.setEstado(nuevoEstado);
        return MensajeResponse.from(mensajeRepository.save(m));
    }

    // Agente responde o añade nota interna
    @Transactional
    public MensajeResponse responderAgente(Long id, RespuestaAgenteRequest req) {
        Mensaje m = mensajeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mensaje no encontrado"));

        RespuestaMensaje respuesta = new RespuestaMensaje();
        respuesta.setMensaje(m);
        respuesta.setTipo(req.getTipo() != null ? req.getTipo() : "agente");
        respuesta.setTexto(req.getTexto());
        respuesta.setFecha(LocalDateTime.now());
        respuestaRepository.save(respuesta);

        // Si es respuesta real (no nota), cambiar estado y enviar email al cliente
        if ("agente".equals(respuesta.getTipo())) {
            m.setEstado("respondidos");
            mensajeRepository.save(m);
            emailService.enviarRespuestaAgente(
                m.getEmail(),
                m.getNombre(),
                m.getMensajeOriginal(),
                req.getTexto(),
                m.getTokenRespuesta()
            );
        }

        return MensajeResponse.from(mensajeRepository.findById(id).get());
    }

    // Obtener datos del hilo por token (para la página pública del cliente)
    @Transactional(readOnly = true)
    public MensajeResponse obtenerPorToken(String token) {
        Mensaje m = mensajeRepository.findByTokenRespuesta(token)
                .orElseThrow(() -> new RuntimeException("Token no válido"));
        return MensajeResponse.from(m);
    }

    // Cliente responde desde su enlace
    @Transactional
    public void responderCliente(String token, RespuestaClienteRequest req) {
        Mensaje m = mensajeRepository.findByTokenRespuesta(token)
                .orElseThrow(() -> new RuntimeException("Token no válido"));

        RespuestaMensaje respuesta = new RespuestaMensaje();
        respuesta.setMensaje(m);
        respuesta.setTipo("cliente");
        respuesta.setTexto(req.getTexto());
        respuesta.setFecha(LocalDateTime.now());
        respuestaRepository.save(respuesta);

        // Volver a estado nuevos para que el agente lo vea
        m.setEstado("nuevos");
        mensajeRepository.save(m);

        // Notificar al agente
        emailService.notificarNuevoMensaje(emailAgente, m.getNombre(), req.getTexto());
    }
}
