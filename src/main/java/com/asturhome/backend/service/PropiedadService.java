package com.asturhome.backend.service;

import com.asturhome.backend.entity.Propiedad;
import com.asturhome.backend.repository.PropiedadRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PropiedadService {

    @Autowired
    private PropiedadRepository repository;

    public List<Propiedad> findAll() {
        return repository.findAll();
    }

    public Optional<Propiedad> findById(Long id) {
        return repository.findById(id);
    }

    public Propiedad save(Propiedad propiedad) {
        return repository.save(propiedad);
    }

    public Optional<Propiedad> update(Long id, Propiedad datos) {
        return repository.findById(id).map(p -> {
            p.setNombre(datos.getNombre());
            p.setTipo(datos.getTipo());
            p.setModalidad(datos.getModalidad());
            p.setEstado(datos.getEstado());
            p.setUbicacion(datos.getUbicacion());
            p.setPrecio(datos.getPrecio());
            p.setHabitaciones(datos.getHabitaciones());
            p.setBanos(datos.getBanos());
            p.setSuperficie(datos.getSuperficie());
            p.setImagenes(datos.getImagenes());
            return repository.save(p);
        });
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) return false;
        repository.deleteById(id);
        return true;
    }
}
