package com.camaras.seguridadcamarasweb.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.camaras.seguridadcamarasweb.model.OrderDetail;



public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
