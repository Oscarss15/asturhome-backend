package com.asturhome.backend.dto;

import com.asturhome.backend.entity.Mensaje;
import com.asturhome.backend.entity.RespuestaMensaje;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class MensajeResponse {

    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String tipoConsulta;
    private String propiedad;
    private String modalidad;
    private String mensaje;
    private String estado;
    private String fecha;
    private List<BurbujaDTO> hilo;

    public static MensajeResponse from(Mensaje m) {
        MensajeResponse r = new MensajeResponse();
        r.id = m.getId();
        r.nombre = m.getNombre();
        r.email = m.getEmail();
        r.telefono = m.getTelefono();
        r.tipoConsulta = m.getTipoConsulta();
        r.propiedad = m.getPropiedad();
        r.modalidad = m.getModalidad();
        r.mensaje = m.getMensajeOriginal();
        r.estado = m.getEstado();
        r.fecha = m.getFecha() != null ? m.getFecha().toLocalDate().toString() : null;
        r.hilo = m.getHilo().stream().map(BurbujaDTO::from).collect(Collectors.toList());
        return r;
    }

    public static class BurbujaDTO {
        private Long id;
        private String tipo;
        private String texto;
        private String hora;

        public static BurbujaDTO from(RespuestaMensaje rm) {
            BurbujaDTO b = new BurbujaDTO();
            b.id = rm.getId();
            b.tipo = rm.getTipo();
            b.texto = rm.getTexto();
            b.hora = rm.getFecha() != null
                ? rm.getFecha().format(DateTimeFormatter.ofPattern("HH:mm"))
                : "";
            return b;
        }

        public Long getId() { return id; }
        public String getTipo() { return tipo; }
        public String getTexto() { return texto; }
        public String getHora() { return hora; }
    }

    public Long getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public String getTelefono() { return telefono; }
    public String getTipoConsulta() { return tipoConsulta; }
    public String getPropiedad() { return propiedad; }
    public String getModalidad() { return modalidad; }
    public String getMensaje() { return mensaje; }
    public String getEstado() { return estado; }
    public String getFecha() { return fecha; }
    public List<BurbujaDTO> getHilo() { return hilo; }
}
