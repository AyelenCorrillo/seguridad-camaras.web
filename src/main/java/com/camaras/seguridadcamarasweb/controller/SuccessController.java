package com.camaras.seguridadcamarasweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SuccessController {

    /**
     * Muestra la página de confirmación después de una compra exitosa.
     */
    @GetMapping("/success")
    public String showSuccessPage() {
        // Devuelve la plantilla success.html
        return "success";
    }
    
}
