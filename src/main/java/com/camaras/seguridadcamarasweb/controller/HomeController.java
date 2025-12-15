package com.camaras.seguridadcamarasweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller
public class HomeController {

    /**
     * Maneja la solicitud de la página de inicio (localhost:8080/)
     */
    @GetMapping("/")
    public String showHomePage() {
        return "index"; // Esto buscará la plantilla: src/main/resources/templates/index.html
    }
    
}
