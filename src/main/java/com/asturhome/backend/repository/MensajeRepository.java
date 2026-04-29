package com.asturhome.backend.repository;

import com.asturhome.backend.entity.Mensaje;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MensajeRepository extends JpaRepository<Mensaje, Long> {
    Optional<Mensaje> findByTokenRespuesta(String token);
}
