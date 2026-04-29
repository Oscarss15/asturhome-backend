package com.asturhome.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mensajes")
public class Mensaje {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String email;
    private String telefono;
    private String tipoConsulta;
    private String propiedad;
    private String modalidad;

    @Column(columnDefinition = "TEXT")
    private String mensajeOriginal;

    private String estado;
    private LocalDateTime fecha;

    @Column(unique = true)
    private String tokenRespuesta;

    @OneToMany(mappedBy = "mensaje", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @OrderBy("fecha ASC")
    private List<RespuestaMensaje> hilo = new ArrayList<>();

    public Mensaje() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoConsulta() { return tipoConsulta; }
    public void setTipoConsulta(String tipoConsulta) { this.tipoConsulta = tipoConsulta; }

    public String getPropiedad() { return propiedad; }
    public void setPropiedad(String propiedad) { this.propiedad = propiedad; }

    public String getModalidad() { return modalidad; }
    public void setModalidad(String modalidad) { this.modalidad = modalidad; }

    public String getMensajeOriginal() { return mensajeOriginal; }
    public void setMensajeOriginal(String mensajeOriginal) { this.mensajeOriginal = mensajeOriginal; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }

    public String getTokenRespuesta() { return tokenRespuesta; }
    public void setTokenRespuesta(String tokenRespuesta) { this.tokenRespuesta = tokenRespuesta; }

    public List<RespuestaMensaje> getHilo() { return hilo; }
    public void setHilo(List<RespuestaMensaje> hilo) { this.hilo = hilo; }
}
