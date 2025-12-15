package com.camaras.seguridadcamarasweb.controller;

import java.security.Principal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.camaras.seguridadcamarasweb.model.Installation;
import com.camaras.seguridadcamarasweb.model.Order;
import com.camaras.seguridadcamarasweb.model.User;
import com.camaras.seguridadcamarasweb.service.InstallationService;
import com.camaras.seguridadcamarasweb.service.OrderService;
import com.camaras.seguridadcamarasweb.service.UserService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private InstallationService installationService;
    
    @Autowired
    private UserService userService; 

    // --- 1. VISTA DEL CARRITO ---

    /**
     * Muestra la vista del carrito actual del usuario.
     */
    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {
        String userEmail = principal.getName();
        User currentUser = userService.findByEmail(userEmail); 
        Order order = orderService.getOrCreatePendingOrder(currentUser);
        
        model.addAttribute("order", order);
        model.addAttribute("installationPrice", installationService.getInstallationPrice());
        
        return "orders/cart"; 
    }
    
    // --- 2. GESTIÓN DE PRODUCTOS ---

    /**
     * Procesa la adición de un producto al carrito (llamado desde el catálogo).
     */
    @PostMapping("/add")
    public String addProductToCart(Principal principal,
                                   @RequestParam("productId") Long productId,
                                   @RequestParam("quantity") int quantity,
                                   RedirectAttributes redirectAttributes) {
        try {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail); 
            
            orderService.addProductToCart(currentUser, productId, quantity);
            
            redirectAttributes.addFlashAttribute("successMessage", "Producto agregado al carrito.");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/catalogo"; 
        } catch (Exception e) {
            // Manejo de excepción de seguridad o error inesperado
            redirectAttributes.addFlashAttribute("errorMessage", 
                                                 "Error al añadir producto: " + e.getMessage());
            return "redirect:/catalogo"; 
        }
        
        return "redirect:/orders/cart";
    }

    /**
     * Procesa la eliminación de un producto del carrito.
     */
    @PostMapping("/remove")
    public String removeProductFromCart(Principal principal,
                                        @RequestParam("productId") Long productId,
                                        RedirectAttributes redirectAttributes) {
        try {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail); 
            
            orderService.removeProductFromCart(currentUser, productId);
            
            redirectAttributes.addFlashAttribute("successMessage", "Producto eliminado del carrito.");
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar producto: " + e.getMessage());
        }
        
        return "redirect:/orders/cart";
    }
    
    // --- 3. GESTIÓN DE INSTALACIÓN (CRÍTICO) ---

    /**
     * Muestra el formulario para seleccionar la fecha y hora de instalación.
     * Maneja el GET /orders/installation/schedule
     */
    @GetMapping("/installation/schedule")
    public String showInstallationForm(Principal principal, Model model, RedirectAttributes redirectAttributes) {
        try {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);
            
            if (currentUser == null) {
                // Error de seguridad si el usuario no se encuentra
                redirectAttributes.addFlashAttribute("errorMessage", "Error de seguridad: Usuario no encontrado en la base de datos.");
                return "redirect:/orders/cart";
            }
            
            Order order = orderService.getOrCreatePendingOrder(currentUser);

            // Inicializa Installation si es nula
            if (order.getInstallation() == null) {
                order.setInstallation(new Installation());
            }
            
            // Atributos necesarios para la vista
            model.addAttribute("installationRequest", order.getInstallation());
            model.addAttribute("installationPrice", installationService.getInstallationPrice());
            model.addAttribute("minDate", LocalDate.now().plusDays(1));
            model.addAttribute("workingHours", "8:00 - 15:00 hs (Lunes a Sábado)");
            model.addAttribute("order", order); 

            // Devuelve la vista renombrada (installation-test.html)
            return "orders/installation-test"; 
            
        } catch (Exception e) {
            // Captura cualquier fallo de inicialización (NPE, BD connection, etc.)
            System.err.println("Error grave al cargar el formulario de instalación: " + e.getMessage());
            e.printStackTrace(); 
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno al cargar la instalación. Mensaje: " + e.getMessage());
            return "redirect:/orders/cart"; 
        }
    }
    
    /**
     * Procesa y valida la solicitud de instalación.
     * Maneja el POST /orders/installation/confirm
     */
    @PostMapping("/installation/confirm")
    public String confirmInstallation(
                                        Principal principal,
                                        @Valid @ModelAttribute("installationRequest") Installation installation, 
                                        BindingResult bindingResult, 
                                        RedirectAttributes redirectAttributes,
                                        Model model) { 

        // 1. Verificar si hay errores de Bean Validation (ej. dirección vacía)
        if (bindingResult.hasErrors()) {
            // Recargar modelo para que la plantilla (installation-test) no falle
            model.addAttribute("installationPrice", installationService.getInstallationPrice());
            model.addAttribute("minDate", LocalDate.now().plusDays(1)); 
            model.addAttribute("workingHours", "8:00 - 15:00 hs (Lunes a Sábado)");
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);
            model.addAttribute("order", orderService.getOrCreatePendingOrder(currentUser));
            
            // Devuelve la vista directamente (installation-test.html)
            return "orders/installation-test"; 
        }

        // 2. Ejecutar la lógica de negocio con manejo robusto de errores
        try {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);
            Order order = orderService.getOrCreatePendingOrder(currentUser);

            orderService.addInstallationToOrder(order, installation);
            
            redirectAttributes.addFlashAttribute("successMessage", "Instalación programada y precio actualizado.");
            return "redirect:/orders/cart";

        } catch (IllegalArgumentException e) {
            // 3. Manejo de errores de negocio (ej. Domingo, o fuera de horario)
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            
            // Redirige al GET para recargar la vista con el mensaje de error flash
            return "redirect:/orders/installation/schedule"; 
            
        } catch (Exception e) { 
            // 4. MANEJO DE ERRORES INESPERADOS (NPE, DB ERROR, Mapeo)
            System.err.println("Error grave al confirmar instalación: " + e.getMessage());
            e.printStackTrace(); 
            
            redirectAttributes.addFlashAttribute("errorMessage", 
                "Error interno. Es probable un fallo de base de datos o mapeo. Mensaje: " + e.getMessage());
            
            // Redirige al GET para recargar la vista con el mensaje de error flash
            return "redirect:/orders/installation/schedule";
        }
    }

    /**
     * Cancela la instalación programada y elimina el costo del pedido.
    */
    @PostMapping("/installation/cancel")
    public String cancelInstallation(Principal principal, RedirectAttributes redirectAttributes) {
        try {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);
            Order order = orderService.getOrCreatePendingOrder(currentUser);

            orderService.removeInstallationFromOrder(order);
            
            redirectAttributes.addFlashAttribute("successMessage", "La instalación ha sido cancelada.");
            
        } catch (Exception e) {
            System.err.println("Error al cancelar la instalación: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "Error al cancelar la instalación. Mensaje: " + e.getMessage());
        }
        
        return "redirect:/orders/cart";
    }

    // --- 4. FINALIZACIÓN DE COMPRA ---
    /**
     * Finaliza la compra.
     */
    @PostMapping("/checkout")
    public String checkoutOrder(Principal principal, 
                                 RedirectAttributes redirectAttributes) {
        try {
            String userEmail = principal.getName();
            User currentUser = userService.findByEmail(userEmail);
            Order order = orderService.getOrCreatePendingOrder(currentUser);
            
            orderService.finalizeOrder(order);
            
            redirectAttributes.addFlashAttribute("successMessage", "¡Compra finalizada con éxito! Gracias por tu pedido.");
            return "redirect:/success";
            
        } catch (Exception e) {
            // LÍNEA TEMPORALMENTE AÑADIDA PARA DIAGNÓSTICO
            e.printStackTrace(); // <--- IMPRIMIRÁ EL ERROR REAL EN TU CONSOLA
            // ---------------------------------------------------------
            redirectAttributes.addFlashAttribute("errorMessage", "Error al procesar la compra. Intente nuevamente.");
            return "redirect:/orders/cart";
        }
    }
}