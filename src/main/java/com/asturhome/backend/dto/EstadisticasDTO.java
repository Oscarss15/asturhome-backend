package com.asturhome.backend.dto;

import java.util.List;
import java.util.Map;

public class EstadisticasDTO {

    private long totalPropiedades;
    private long totalPropiedadesAnterior;
    private long leadsEsteMes;
    private long leadsAnteriorMes;
    private long ventasEsteMes;
    private long ventasAnteriorMes;
    private double ingresosEstimados;
    private double ingresosAnteriorMes;

    private double ratioVenta;
    private long mediaReservasDias;
    private long nuevosLeadsHoy;

    private Map<String, Long> estadoCartera;
    private List<MesStat> ventasPorMes;
    private Map<String, Double> precioMedioPorTipo;
    private Map<String, Long> distribucionZona;
    private List<AltaDia> altasRecientes;
    private List<TopPropiedad> topPropiedades;

    public EstadisticasDTO() {}

    // ── Getters & Setters ──────────────────────────────

    public long getTotalPropiedades() { return totalPropiedades; }
    public void setTotalPropiedades(long totalPropiedades) { this.totalPropiedades = totalPropiedades; }

    public long getTotalPropiedadesAnterior() { return totalPropiedadesAnterior; }
    public void setTotalPropiedadesAnterior(long v) { this.totalPropiedadesAnterior = v; }

    public long getLeadsEsteMes() { return leadsEsteMes; }
    public void setLeadsEsteMes(long leadsEsteMes) { this.leadsEsteMes = leadsEsteMes; }

    public long getLeadsAnteriorMes() { return leadsAnteriorMes; }
    public void setLeadsAnteriorMes(long v) { this.leadsAnteriorMes = v; }

    public long getVentasEsteMes() { return ventasEsteMes; }
    public void setVentasEsteMes(long ventasEsteMes) { this.ventasEsteMes = ventasEsteMes; }

    public long getVentasAnteriorMes() { return ventasAnteriorMes; }
    public void setVentasAnteriorMes(long v) { this.ventasAnteriorMes = v; }

    public double getIngresosEstimados() { return ingresosEstimados; }
    public void setIngresosEstimados(double ingresosEstimados) { this.ingresosEstimados = ingresosEstimados; }

    public double getIngresosAnteriorMes() { return ingresosAnteriorMes; }
    public void setIngresosAnteriorMes(double v) { this.ingresosAnteriorMes = v; }

    public double getRatioVenta() { return ratioVenta; }
    public void setRatioVenta(double ratioVenta) { this.ratioVenta = ratioVenta; }

    public long getMediaReservasDias() { return mediaReservasDias; }
    public void setMediaReservasDias(long mediaReservasDias) { this.mediaReservasDias = mediaReservasDias; }

    public long getNuevosLeadsHoy() { return nuevosLeadsHoy; }
    public void setNuevosLeadsHoy(long nuevosLeadsHoy) { this.nuevosLeadsHoy = nuevosLeadsHoy; }

    public Map<String, Long> getEstadoCartera() { return estadoCartera; }
    public void setEstadoCartera(Map<String, Long> estadoCartera) { this.estadoCartera = estadoCartera; }

    public List<MesStat> getVentasPorMes() { return ventasPorMes; }
    public void setVentasPorMes(List<MesStat> ventasPorMes) { this.ventasPorMes = ventasPorMes; }

    public Map<String, Double> getPrecioMedioPorTipo() { return precioMedioPorTipo; }
    public void setPrecioMedioPorTipo(Map<String, Double> precioMedioPorTipo) { this.precioMedioPorTipo = precioMedioPorTipo; }

    public Map<String, Long> getDistribucionZona() { return distribucionZona; }
    public void setDistribucionZona(Map<String, Long> distribucionZona) { this.distribucionZona = distribucionZona; }

    public List<AltaDia> getAltasRecientes() { return altasRecientes; }
    public void setAltasRecientes(List<AltaDia> altasRecientes) { this.altasRecientes = altasRecientes; }

    public List<TopPropiedad> getTopPropiedades() { return topPropiedades; }
    public void setTopPropiedades(List<TopPropiedad> topPropiedades) { this.topPropiedades = topPropiedades; }

    // ── Clases internas ────────────────────────────────

    public static class MesStat {
        private String mes;
        private long ventas;
        private long alquileres;

        public MesStat(String mes, long ventas, long alquileres) {
            this.mes = mes;
            this.ventas = ventas;
            this.alquileres = alquileres;
        }

        public String getMes() { return mes; }
        public long getVentas() { return ventas; }
        public long getAlquileres() { return alquileres; }
    }

    public static class TopPropiedad {
        private Long id;
        private String nombre;
        private long vistas;
        private String imagen;

        public TopPropiedad(Long id, String nombre, long vistas, String imagen) {
            this.id = id;
            this.nombre = nombre;
            this.vistas = vistas;
            this.imagen = imagen;
        }

        public Long getId() { return id; }
        public String getNombre() { return nombre; }
        public long getVistas() { return vistas; }
        public String getImagen() { return imagen; }
    }

    public static class AltaDia {
        private String fecha;
        private long cantidad;

        public AltaDia(String fecha, long cantidad) {
            this.fecha = fecha;
            this.cantidad = cantidad;
        }

        public String getFecha() { return fecha; }
        public long getCantidad() { return cantidad; }
    }
}
