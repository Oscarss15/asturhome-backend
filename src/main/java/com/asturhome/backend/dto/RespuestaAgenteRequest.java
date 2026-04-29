package com.asturhome.backend.dto;

public class RespuestaAgenteRequest {
    private String texto;
    private String tipo; // agente o nota

    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
}
