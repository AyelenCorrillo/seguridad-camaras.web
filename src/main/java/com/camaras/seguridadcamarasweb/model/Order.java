package com.camaras.seguridadcamarasweb.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.camaras.seguridadcamarasweb.enums.OrderStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;


@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relación N:1 con User (Muchos pedidos pertenecen a un usuario)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private BigDecimal totalPrice;
    
    private LocalDate orderDate;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    // Relación 1:N con OrderDetail (Un pedido tiene muchos ítems)
    // CascadeType.ALL asegura que los detalles se guarden o borren con el pedido.
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    // Relación 1:1 con Installation (Un pedido puede tener una instalación asociada)
    @OneToOne(mappedBy = "order")
    private Installation installation;

    // --- Constructor vacío ---
    public Order() {}

    // --- Getters y Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public BigDecimal getTotalPrice() { return totalPrice; }
    public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }

    public LocalDate getOrderDate() { return orderDate; }
    public void setOrderDate(LocalDate orderDate) { this.orderDate = orderDate; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public List<OrderDetail> getOrderDetails() { return orderDetails; }
    public void setOrderDetails(List<OrderDetail> orderDetails) { this.orderDetails = orderDetails; }

    public Installation getInstallation() { return installation; }
    public void setInstallation(Installation installation) { this.installation = installation; }

    public BigDecimal getTotal() {
    return this.totalPrice; 
    }
    
    public Integer getTotalProducts() { return this.getOrderDetails()
        .stream() 
        .mapToInt(OrderDetail::getQuantity)
        .sum();
    }
    
}
