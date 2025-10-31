package org.lumbi.ejercicio.repository;

import java.util.List;
import java.util.Optional;

import org.lumbi.ejercicio.domain.UserDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserDomain, Long> {

    // Buscar usuario por username
    Optional<UserDomain> findByUsername(String username);

    // Verificar si existe un usuario con ese username
    boolean existsByUsername(String username);

    // Buscar usuarios cuyo username contenga un texto
    List<UserDomain> findByUsernameContainingIgnoreCase(String username);
}