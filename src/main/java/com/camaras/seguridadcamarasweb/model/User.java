package com.camaras.seguridadcamarasweb.model;

import java.util.List;

import com.camaras.seguridadcamarasweb.enums.UserRole;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users") // Nombre de la tabla en la DB
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // AÑADIDO: El nombre no puede ser null ni solo espacios.
    @NotBlank(message = "El nombre es obligatorio.")
    private String name;
    
    @Column(unique = true, nullable = false)
    // AÑADIDO: Debe ser un formato de email válido y no puede ser vacío.
    @Email(message = "Debe ser una dirección de correo válida.")
    @NotBlank(message = "El email es obligatorio.")
    private String email;
    
    @Column(nullable = false)
    // AÑADIDO: No puede ser vacío y debe tener al menos 6 caracteres.
    @NotBlank(message = "La contraseña es obligatoria.")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres.")
    private String password; // ¡Cifrada!

    @Enumerated(EnumType.STRING)
    private UserRole role; 

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Order> orders;

    // --- Constructor vacío es necesario para JPA ---
    public User() {}
    
    // --- Getters y Setters van aquí ---
    // (Puedes usar Lombok para generarlos automáticamente si lo incluiste)

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }

    public List<Order> getOrders() { return orders; }
    public void setOrders(List<Order> orders) { this.orders = orders; }
    
}
