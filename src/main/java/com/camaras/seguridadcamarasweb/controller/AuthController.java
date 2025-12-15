package com.camaras.seguridadcamarasweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.camaras.seguridadcamarasweb.model.User;
import com.camaras.seguridadcamarasweb.service.UserService;

import jakarta.validation.Valid;



@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    // --- RUTA DE LOGIN ---

    /**
     * Muestra el formulario de login. Spring Security maneja el POST automáticamente.
     */
    @GetMapping("/login")
    public String showLoginPage() {
        // Retorna la vista: src/main/resources/templates/login.html
        return "login"; 
    }

    // --- RUTA DE REGISTRO ---

    /**
     * Muestra el formulario para registrar un nuevo usuario.
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        // Pasa un objeto User vacío para que el formulario lo rellene
        model.addAttribute("user", new User());
        // Retorna la vista: src/main/resources/templates/register.html
        return "register";
    }

    /**
     * Procesa la solicitud POST del formulario de registro.
     */
   @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user, 
                               BindingResult bindingResult, // Captura errores de validación
                               RedirectAttributes redirectAttributes,
                               Model model) { // Añadido 'Model' para retornar errores a la vista
        
        // 1. Verificar si hay errores de Bean Validation
        if (bindingResult.hasErrors()) {
            // Si hay errores (ej. password muy corta), regresa al formulario
            // Spring ya pone los errores en el 'Model'
            return "register"; 
        }

        try {
            // 2. Si no hay errores, procede con el registro
            userService.registerNewClient(user);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                                                "¡Registro exitoso! Por favor, inicia sesión.");
            
            return "redirect:/login";

        } catch (Exception e) {
            // 3. Manejo de errores de negocio (ej. email duplicado)
            // Usamos 'Model' aquí porque vamos a retornar directamente la vista 'register'
            model.addAttribute("errorMessage", 
                                                "Error al registrar: El email ya está en uso.");
            // Volvemos a la vista, los datos validados del usuario ya están en el modelo
            return "register"; 
        }
    }
}
