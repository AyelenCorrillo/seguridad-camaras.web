package com.camaras.seguridadcamarasweb.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.camaras.seguridadcamarasweb.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Buscar el pedido (carrito) que está PENDIENTE para un usuario específico.
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = 'PENDIENTE'")
    Optional<Order> findPendingOrderByUser(@Param("userId") Long userId);
}
