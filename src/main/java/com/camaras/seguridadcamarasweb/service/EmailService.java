package com.camaras.seguridadcamarasweb.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // Obtener el email del remitente configurado en application.properties
    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * Envía un email con la consulta de un usuario al administrador.
     * @param name Nombre del usuario.
     * @param userEmail Email del usuario (para responderle).
     * @param subject Asunto de la consulta.
     * @param content Contenido del mensaje.
     */
    public void sendContactEmail(String name, String userEmail, String subject, String content) {
        
        SimpleMailMessage message = new SimpleMailMessage();
        
        // El email del destinatario es la cuenta configurada en application.properties
        message.setTo(senderEmail); 
        
        // El asunto en el buzón del administrador
        message.setSubject("Nueva Consulta Web: " + subject);
        
        // El cuerpo del mensaje
        String fullContent = "Recibiste una nueva consulta de:\n" +
                            "Nombre: " + name + "\n" +
                            "Email: " + userEmail + "\n" +
                            "Mensaje:\n" + content; 
        message.setText(fullContent);

        // Opcional: Establecer el campo "Reply-To" para responder fácilmente al usuario
        message.setReplyTo(userEmail);
        
        mailSender.send(message);
    }
}
