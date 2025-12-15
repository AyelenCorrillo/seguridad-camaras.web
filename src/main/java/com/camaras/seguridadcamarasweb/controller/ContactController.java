package com.camaras.seguridadcamarasweb.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.camaras.seguridadcamarasweb.service.EmailService;



@Controller
public class ContactController {

    @Autowired
    private EmailService emailService;

    /**
     * Muestra la vista del formulario de contacto.
     */
    @GetMapping("/contact")
    public String showContactForm() {
        return "contact"; // Apunta a src/main/resources/templates/contact.html
    }

    /**
     * Procesa la solicitud POST y envía el email.
     */
    @PostMapping("/contact/send")
    public String sendContactEmail(@RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String subject,
                                   @RequestParam String message,
                                   RedirectAttributes redirectAttributes) {
        try {
            emailService.sendContactEmail(name, email, subject, message);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                                                "¡Mensaje enviado con éxito! Te responderemos pronto.");
        } catch (Exception e) {
            System.err.println("Error al enviar email: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", 
                                                "Error al enviar el mensaje. Intenta más tarde.");
        }
        
        return "redirect:/contact";
    }
}
