package com.camaras.seguridadcamarasweb.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.camaras.seguridadcamarasweb.enums.OrderStatus;
import com.camaras.seguridadcamarasweb.model.Installation;
import com.camaras.seguridadcamarasweb.model.Order;
import com.camaras.seguridadcamarasweb.model.OrderDetail;
import com.camaras.seguridadcamarasweb.model.Product;
import com.camaras.seguridadcamarasweb.model.User;
import com.camaras.seguridadcamarasweb.repository.InstallationRepository;
import com.camaras.seguridadcamarasweb.repository.OrderRepository;


@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductService productService; 
    
    @Autowired
    private InstallationService installationService; 

    @Autowired
    private InstallationRepository installationRepository;

    // --- Métodos Principales del Servicio de Pedidos ---

    /**
     * Busca o crea un carrito (Pedido en estado PENDIENTE) para el usuario.
     */
    public Order getOrCreatePendingOrder(User user) {
        // Buscar un pedido PENDIENTE para el usuario logueado.
        // El OrderRepository necesita el ID del usuario
        Optional<Order> pendingOrder = orderRepository.findPendingOrderByUser(user.getId());
        
        if (pendingOrder.isPresent()) {
            Order order = pendingOrder.get();
            if (order.getOrderDetails() == null) {
                order.setOrderDetails(new ArrayList<>());
            }
            return order;
        } else {
            // Crear un nuevo carrito si no existe
            Order newOrder = new Order();
            newOrder.setUser(user);
            newOrder.setStatus(OrderStatus.PENDIENTE);
            newOrder.setTotalPrice(BigDecimal.ZERO);
            return orderRepository.save(newOrder);
        }
    }
    
    /**
     * Añade un producto al carrito del usuario, actualizando cantidades y totales según corresponda.
     */
    public Order addProductToCart(User user, Long productId, int quantity) {
        // 1. Obtener o crear el carrito (Pedido en estado PENDIENTE)
        Order order = getOrCreatePendingOrder(user);
        
        // 2. Obtener el producto del catálogo
        Product product = productService.findProductById(productId)
                                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado."));

        // 3. Buscar si el producto ya está en los detalles del pedido
        Optional<OrderDetail> existingDetail = order.getOrderDetails().stream()
                .filter(detail -> detail.getProduct().getId().equals(productId))
                .findFirst();

        if (existingDetail.isPresent()) {
            // El producto ya está: Actualizar cantidad y subtotal
            OrderDetail detail = existingDetail.get();
            detail.setQuantity(detail.getQuantity() + quantity);
            detail.setUnitPrice(product.getUnitPrice());
            
            // Recalcular subtotal
            BigDecimal newQuantity = new BigDecimal(detail.getQuantity());
            detail.setSubtotal(product.getUnitPrice().multiply(newQuantity).setScale(2, RoundingMode.HALF_UP));

        } else {
            // El producto no está: Crear un nuevo detalle del pedido
            OrderDetail newDetail = new OrderDetail();
            newDetail.setOrder(order);
            newDetail.setProduct(product);
            newDetail.setQuantity(quantity);
            // ASIGNAR EL PRECIO UNITARIO DEL PRODUCTO
            newDetail.setUnitPrice(product.getUnitPrice());

            // Calcular subtotal inicial
            BigDecimal newQuantity = new BigDecimal(quantity);
            newDetail.setSubtotal(product.getUnitPrice().multiply(newQuantity).setScale(2, RoundingMode.HALF_UP));
            
            // Añadir al pedido
            order.getOrderDetails().add(newDetail);
        }

        // 4. Recalcular el total general del pedido y guardar
        return recalculateOrderTotal(order);
    }

    /**
 * Elimina un producto específico del carrito (OrderDetail) y recalcula el total.
 */
    public Order removeProductFromCart(User user, Long productId) {
        Order order = getOrCreatePendingOrder(user);
    
        // 1. Buscar el OrderDetail a eliminar
        boolean removed = order.getOrderDetails().removeIf(detail -> detail.getProduct().getId().equals(productId));

        if (removed) {
            // 2. Recalcular el total general y guardar
            return recalculateOrderTotal(order);
        } else {
            // Si no se eliminó nada, devolver el pedido sin cambios.
            return order;
        }
    }


    
    /**
     * Recalcula el precio total del pedido (productos + instalación si aplica).
     */
    public Order recalculateOrderTotal(Order order) {
        // Sumar el subtotal de todos los OrderDetails
        BigDecimal productsTotal = order.getOrderDetails().stream()
                .map(OrderDetail::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal installationCost = BigDecimal.ZERO;
        if (order.getInstallation() != null) {
            // Obtener el precio de la instalación (ya fijado en Installation)
            installationCost = order.getInstallation().getInstallationPrice();
        }

        // Sumar y redondear el total final a 2 decimales
        BigDecimal finalTotal = productsTotal.add(installationCost).setScale(2, RoundingMode.HALF_UP);
        order.setTotalPrice(finalTotal);
        
        // Guarda el Order (y por CascadeType.ALL, guardará o actualizará los OrderDetails)
        return orderRepository.save(order);
    }
    
    /**
     * Asocia la solicitud de instalación al pedido, valida la fecha/hora y recalcula el precio final.
     */
    public Order addInstallationToOrder(Order order, Installation installation) {
        
        // 1. Validar la hora de instalación
        if (!installationService.isValidInstallationTime(installation.getDesiredDate(), installation.getDesiredTime())) {
            throw new IllegalArgumentException("El horario seleccionado no es válido (Lunes a Sábado, 8:00 a 15:00).");
        }
        
        // 2. Asignar precio y relación
        // Obtener el precio base del servicio
        installation.setInstallationPrice(BigDecimal.valueOf(installationService.getInstallationPrice()).setScale(2, RoundingMode.HALF_UP));
        installation.setOrder(order);
        order.setInstallation(installation);
        installationRepository.save(installation);
        
        // 3. Recalcular el total del pedido (incluyendo la instalación)
        return recalculateOrderTotal(order);
    }

    /**
     * Remueve la instalación asociada al pedido y recalcula el precio total.
    */
    public Order removeInstallationFromOrder(Order order) {
        Installation installation = order.getInstallation();

        if (installation != null) {
            // 1. Quitar la referencia bidireccional
            order.setInstallation(null);
            installation.setOrder(null); // No es estrictamente necesario, pero es buena práctica
            
            // 2. Eliminar la entidad de la base de datos (CRÍTICO)
            installationRepository.delete(installation);
        }
        
        // 3. Recalcular el total (que será sin el costo de instalación)
        return recalculateOrderTotal(order);
    }


    /**
     * Cambia el estado del pedido a CONFIRMADO, finalizando la compra.
     */
    public Order finalizeOrder(Order order) {

        if (order.getOrderDetails().isEmpty()) {
            throw new IllegalStateException("No se puede finalizar un pedido sin productos en el carrito.");
        }

        // Asignar la fecha de la orden (CRÍTICO si es NOT NULL en la BD)
        order.setOrderDate(LocalDate.now());
        
        // Cambiar el estado a CONFIRMADO
        order.setStatus(OrderStatus.CONFIRMADO);

        // Aseguramos que el total final esté actualizado y seteado
        order = recalculateOrderTotal(order); 
        
        // 2. Guardar el pedido actualizado
        return orderRepository.save(order);
        
        // NOTA: Aquí iría la lógica de descuento de stock de productos 
        // y el envío de email de confirmación, si se implementaran.
    }
}
