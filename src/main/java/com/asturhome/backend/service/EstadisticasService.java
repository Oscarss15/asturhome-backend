package com.asturhome.backend.service;

import com.asturhome.backend.dto.EstadisticasDTO;
import com.asturhome.backend.entity.Mensaje;
import com.asturhome.backend.entity.Propiedad;
import com.asturhome.backend.repository.MensajeRepository;
import com.asturhome.backend.repository.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EstadisticasService {

    @Autowired private PropiedadRepository propiedadRepository;
    @Autowired private MensajeRepository mensajeRepository;

    public EstadisticasDTO calcular() {
        List<Propiedad> propiedades = propiedadRepository.findAll();
        List<Mensaje> mensajes = mensajeRepository.findAll();

        EstadisticasDTO dto = new EstadisticasDTO();

        LocalDateTime ahora = LocalDateTime.now();
        int mesActual = ahora.getMonthValue();
        int anioActual = ahora.getYear();
        LocalDateTime anteriores = ahora.minusMonths(1);
        int mesAnterior = anteriores.getMonthValue();
        int anioAnterior = anteriores.getYear();

        // Total propiedades
        dto.setTotalPropiedades(propiedades.size());

        // Total propiedades al final del mes anterior
        long propAnterior = propiedades.stream()
            .filter(p -> p.getFechaAlta() != null && (
                p.getFechaAlta().getYear() < anioActual ||
                (p.getFechaAlta().getYear() == anioActual && p.getFechaAlta().getMonthValue() < mesActual)
            ))
            .count();
        dto.setTotalPropiedadesAnterior(propAnterior);

        // Leads este mes
        long leads = mensajes.stream()
            .filter(m -> m.getFecha() != null
                && m.getFecha().getMonthValue() == mesActual
                && m.getFecha().getYear() == anioActual)
            .count();
        dto.setLeadsEsteMes(leads);

        // Leads mes anterior
        long leadsAnterior = mensajes.stream()
            .filter(m -> m.getFecha() != null
                && m.getFecha().getMonthValue() == mesAnterior
                && m.getFecha().getYear() == anioAnterior)
            .count();
        dto.setLeadsAnteriorMes(leadsAnterior);

        // Ventas/alquileres cerrados este mes
        long ventasMes = propiedades.stream()
            .filter(p -> (p.getEstado().equals("vendido") || p.getEstado().equals("alquilado"))
                && p.getFechaAlta() != null
                && p.getFechaAlta().getMonthValue() == mesActual
                && p.getFechaAlta().getYear() == anioActual)
            .count();
        dto.setVentasEsteMes(ventasMes);

        // Ventas/alquileres mes anterior
        long ventasAnterior = propiedades.stream()
            .filter(p -> (p.getEstado().equals("vendido") || p.getEstado().equals("alquilado"))
                && p.getFechaAlta() != null
                && p.getFechaAlta().getMonthValue() == mesAnterior
                && p.getFechaAlta().getYear() == anioAnterior)
            .count();
        dto.setVentasAnteriorMes(ventasAnterior);

        // Ingresos estimados: suma de precios de propiedades vendidas * comisión 3%
        double ingresos = propiedades.stream()
            .filter(p -> p.getEstado().equals("vendido") && p.getPrecio() != null && p.getModalidad().equals("VENTA"))
            .mapToDouble(p -> p.getPrecio() * 0.03)
            .sum();
        dto.setIngresosEstimados(Math.round(ingresos * 10.0) / 10.0);

        // Ingresos mes anterior
        double ingresosAnterior = propiedades.stream()
            .filter(p -> p.getEstado().equals("vendido") && p.getPrecio() != null
                && p.getModalidad().equals("VENTA")
                && p.getFechaAlta() != null
                && p.getFechaAlta().getMonthValue() == mesAnterior
                && p.getFechaAlta().getYear() == anioAnterior)
            .mapToDouble(p -> p.getPrecio() * 0.03)
            .sum();
        dto.setIngresosAnteriorMes(Math.round(ingresosAnterior * 10.0) / 10.0);

        // Ratio de venta: (vendido + alquilado) / total * 100
        long cerradas = propiedades.stream()
            .filter(p -> p.getEstado().equals("vendido") || p.getEstado().equals("alquilado"))
            .count();
        double ratio = propiedades.isEmpty() ? 0 : Math.round((cerradas * 1000.0) / propiedades.size()) / 10.0;
        dto.setRatioVenta(ratio);

        // Media días en reserva: promedio de días desde fechaAlta para propiedades en estado "reservado"
        OptionalDouble mediaReservas = propiedades.stream()
            .filter(p -> "reservado".equals(p.getEstado()) && p.getFechaAlta() != null)
            .mapToLong(p -> ChronoUnit.DAYS.between(p.getFechaAlta(), ahora))
            .average();
        dto.setMediaReservasDias(Math.round(mediaReservas.orElse(0)));

        // Nuevos leads hoy
        LocalDate hoyDate = LocalDate.now();
        long leadsHoy = mensajes.stream()
            .filter(m -> m.getFecha() != null && m.getFecha().toLocalDate().equals(hoyDate))
            .count();
        dto.setNuevosLeadsHoy(leadsHoy);

        // Estado cartera
        Map<String, Long> estados = propiedades.stream()
            .collect(Collectors.groupingBy(Propiedad::getEstado, Collectors.counting()));
        dto.setEstadoCartera(estados);

        // Ventas y alquileres por mes (últimos 6 meses)
        List<EstadisticasDTO.MesStat> ventasPorMes = new ArrayList<>();
        for (int i = 11; i >= 0; i--) {
            LocalDateTime ref = ahora.minusMonths(i);
            int mes = ref.getMonthValue();
            int anio = ref.getYear();
            String nombreMes = Month.of(mes).getDisplayName(TextStyle.SHORT, new Locale("es", "ES"))
                .toUpperCase().replace(".", "");

            long ventas = propiedades.stream()
                .filter(p -> "VENTA".equals(p.getModalidad())
                    && p.getFechaAlta() != null
                    && p.getFechaAlta().getMonthValue() == mes
                    && p.getFechaAlta().getYear() == anio)
                .count();

            long alquileres = propiedades.stream()
                .filter(p -> "ALQUILER".equals(p.getModalidad())
                    && p.getFechaAlta() != null
                    && p.getFechaAlta().getMonthValue() == mes
                    && p.getFechaAlta().getYear() == anio)
                .count();

            ventasPorMes.add(new EstadisticasDTO.MesStat(nombreMes, ventas, alquileres));
        }
        dto.setVentasPorMes(ventasPorMes);

        // Precio medio por tipo (solo VENTA)
        Map<String, Double> preciosPorTipo = propiedades.stream()
            .filter(p -> "VENTA".equals(p.getModalidad()) && p.getPrecio() != null)
            .collect(Collectors.groupingBy(
                Propiedad::getTipo,
                Collectors.averagingDouble(Propiedad::getPrecio)
            ));
        Map<String, Double> preciosRedondeados = new LinkedHashMap<>();
        preciosPorTipo.forEach((k, v) -> preciosRedondeados.put(k, Math.round(v * 100.0) / 100.0));
        dto.setPrecioMedioPorTipo(preciosRedondeados);

        // Distribución por zona
        Map<String, Long> zonas = new LinkedHashMap<>();
        zonas.put("Oviedo", 0L);
        zonas.put("Gijón", 0L);
        zonas.put("Avilés", 0L);
        zonas.put("Otros", 0L);

        for (Propiedad p : propiedades) {
            String ub = p.getUbicacion() != null ? p.getUbicacion().toLowerCase() : "";
            if (ub.contains("oviedo")) zonas.merge("Oviedo", 1L, Long::sum);
            else if (ub.contains("gij")) zonas.merge("Gijón", 1L, Long::sum);
            else if (ub.contains("avil")) zonas.merge("Avilés", 1L, Long::sum);
            else zonas.merge("Otros", 1L, Long::sum);
        }
        dto.setDistribucionZona(zonas);

        // Top 3 propiedades por vistas
        List<EstadisticasDTO.TopPropiedad> top = propiedades.stream()
            .sorted(Comparator.comparingLong(Propiedad::getVistas).reversed())
            .limit(3)
            .map(p -> new EstadisticasDTO.TopPropiedad(p.getId(), p.getNombre(), p.getVistas(),
                    p.getImagenes() != null && !p.getImagenes().isEmpty() ? p.getImagenes().get(0) : null))
            .collect(Collectors.toList());
        dto.setTopPropiedades(top);

        // Altas recientes (últimos 7 días agrupadas por día)
        LocalDate hoy = LocalDate.now();
        List<EstadisticasDTO.AltaDia> altas = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate dia = hoy.minusDays(i);
            long count = propiedades.stream()
                .filter(p -> p.getFechaAlta() != null && p.getFechaAlta().toLocalDate().equals(dia))
                .count();
            if (count > 0) {
                String label = i == 0 ? "Hoy" : dia.getDayOfWeek()
                    .getDisplayName(TextStyle.FULL, new Locale("es", "ES")) + ", " +
                    dia.getDayOfMonth() + " " +
                    dia.getMonth().getDisplayName(TextStyle.SHORT, new Locale("es", "ES"));
                altas.add(new EstadisticasDTO.AltaDia(label, count));
            }
        }
        dto.setAltasRecientes(altas);

        return dto;
    }
}
