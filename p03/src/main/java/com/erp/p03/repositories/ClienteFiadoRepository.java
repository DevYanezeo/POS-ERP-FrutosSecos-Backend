package com.erp.p03.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.erp.p03.entities.ClienteFiadoEntity;
import java.util.Optional;
import java.util.List;

@Repository
public interface ClienteFiadoRepository extends JpaRepository<ClienteFiadoEntity, Integer> {
    Optional<ClienteFiadoEntity> findByTelefono(String telefono);
    List<ClienteFiadoEntity> findByActivoTrue();
}
