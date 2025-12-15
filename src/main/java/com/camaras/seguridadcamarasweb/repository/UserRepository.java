package com.camaras.seguridadcamarasweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camaras.seguridadcamarasweb.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // Método que Spring Data JPA genera automáticamente:
    // Busca un usuario por su email, necesario para el login.
    Optional<User> findByEmailIgnoreCase(String email);
}
