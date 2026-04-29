package com.asturhome.backend.controller;

import com.asturhome.backend.entity.Propiedad;
import com.asturhome.backend.service.PropiedadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/propiedades")
public class PropiedadController {

    @Autowired
    private PropiedadService service;

    @GetMapping
    public List<Propiedad> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Propiedad> getById(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Propiedad create(@RequestBody Propiedad propiedad) {
        return service.save(propiedad);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Propiedad> update(@PathVariable Long id, @RequestBody Propiedad propiedad) {
        return service.update(id, propiedad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!service.delete(id)) return ResponseEntity.notFound().build();
        return ResponseEntity.noContent().build();
    }
}
