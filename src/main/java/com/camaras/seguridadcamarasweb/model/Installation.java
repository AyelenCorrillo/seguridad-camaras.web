package com.camaras.seguridadcamarasweb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.NotBlank;


@Entity
public class Installation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false) // Clave foránea en la tabla Installation
    private Order order;           // Relación 1:1 con Pedido

    @Column(nullable = false)
    @NotBlank(message = "La dirección de instalación es obligatoria.") // Validación
    private String address;

    @Column(nullable = false)
    private LocalDate desiredDate; // Para la restricción de Lunes a Sábado

    @Column(nullable = false)
    private LocalTime desiredTime; // Para la restricción de 8:00 a 15:00 hs

    @Column(nullable = false)
    private BigDecimal installationPrice; // Precio fijo del servicio de instalación

    // --- Constructor vacío ---
    public Installation() {}

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Order getOrder() { return order; }
    public void setOrder(Order order) { this.order = order; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDesiredDate() { return desiredDate; }
    public void setDesiredDate(LocalDate desiredDate) { this.desiredDate = desiredDate; }

    public LocalTime getDesiredTime() { return desiredTime; }
    public void setDesiredTime(LocalTime desiredTime) { this.desiredTime = desiredTime; }

    public BigDecimal getInstallationPrice() { return installationPrice; }
    public void setInstallationPrice(BigDecimal installationPrice) { this.installationPrice = installationPrice; }
    
}
