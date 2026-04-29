package com.asturhome.backend.controller;

import com.asturhome.backend.dto.EstadisticasDTO;
import com.asturhome.backend.service.EstadisticasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/estadisticas")
public class EstadisticasController {

    @Autowired
    private EstadisticasService service;

    @GetMapping
    public EstadisticasDTO getEstadisticas() {
        return service.calcular();
    }
}
