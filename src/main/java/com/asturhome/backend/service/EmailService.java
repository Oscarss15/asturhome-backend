package com.asturhome.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public void enviarRespuestaAgente(String destinatario, String nombreCliente,
                                      String mensajeOriginal, String respuesta,
                                      String token) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject("Respuesta a tu consulta - Asturhome");

            String enlace = frontendUrl + "/responder/" + token;

            String html = """
                <div style="font-family: Inter, sans-serif; max-width: 560px; margin: 0 auto; padding: 32px 24px; background: #f5f7fa;">
                  <div style="background: #ffffff; border-radius: 16px; padding: 40px; border: 1px solid #eaecf0;">
                    <h2 style="color: #419383; margin: 0 0 8px; font-size: 22px;">Asturhome</h2>
                    <p style="color: #6b7280; margin: 0 0 28px; font-size: 14px;">Hemos respondido a tu consulta.</p>

                    <div style="background: #f9fafb; border-radius: 10px; padding: 16px; margin-bottom: 20px;">
                      <p style="font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.6px; color: #9ca3af; margin: 0 0 8px;">Tu mensaje</p>
                      <p style="font-size: 14px; color: #374151; font-style: italic; margin: 0;">"%s"</p>
                    </div>

                    <div style="background: #e6f4f1; border-radius: 12px; padding: 16px; margin-bottom: 28px;">
                      <p style="font-size: 11px; font-weight: 700; text-transform: uppercase; letter-spacing: 0.6px; color: #419383; margin: 0 0 8px;">Respuesta de Asturhome</p>
                      <p style="font-size: 14px; color: #1e3a5f; margin: 0;">%s</p>
                    </div>

                    <a href="%s"
                       style="display: inline-block; background: #419383; color: #ffffff; text-decoration: none;
                              padding: 13px 28px; border-radius: 8px; font-size: 14px; font-weight: 600;">
                      Responder
                    </a>

                    <p style="color: #9ca3af; font-size: 12px; margin-top: 24px;">
                      Si el botón no funciona, copia este enlace en tu navegador:<br>
                      <a href="%s" style="color: #419383;">%s</a>
                    </p>
                  </div>
                </div>
                """.formatted(mensajeOriginal, respuesta, enlace, enlace, enlace);

            helper.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("Error enviando email: " + e.getMessage());
        }
    }

    public void enviarResetPassword(String destinatario, String enlace) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(destinatario);
            helper.setSubject("Restablecer contraseña - Asturhome");

            String html = """
                <div style="font-family: Inter, sans-serif; max-width: 560px; margin: 0 auto; padding: 32px 24px; background: #f5f7fa;">
                  <div style="background: #ffffff; border-radius: 16px; padding: 40px; border: 1px solid #eaecf0;">
                    <h2 style="color: #419383; margin: 0 0 8px; font-size: 22px;">Asturhome</h2>
                    <p style="color: #374151; margin: 0 0 24px; font-size: 15px;">
                      Hemos recibido una solicitud para restablecer la contraseña de tu cuenta.<br>
                      Este enlace es válido durante <strong>30 minutos</strong>.
                    </p>
                    <a href="%s"
                       style="display: inline-block; background: #419383; color: #ffffff; text-decoration: none;
                              padding: 13px 28px; border-radius: 8px; font-size: 14px; font-weight: 600;">
                      Restablecer contraseña
                    </a>
                    <p style="color: #9ca3af; font-size: 12px; margin-top: 24px;">
                      Si no has solicitado este cambio, ignora este correo.<br>
                      Si el botón no funciona, copia este enlace:<br>
                      <a href="%s" style="color: #419383;">%s</a>
                    </p>
                  </div>
                </div>
                """.formatted(enlace, enlace, enlace);

            helper.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("Error enviando email de reset: " + e.getMessage());
        }
    }

    public void notificarNuevoMensaje(String destinatarioAgente, String nombreCliente, String mensajeTexto) {
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");

            helper.setFrom(from);
            helper.setTo(destinatarioAgente);
            helper.setSubject("Nuevo mensaje de " + nombreCliente + " - Asturhome");

            String html = """
                <div style="font-family: Inter, sans-serif; max-width: 560px; margin: 0 auto; padding: 32px 24px; background: #f5f7fa;">
                  <div style="background: #ffffff; border-radius: 16px; padding: 40px; border: 1px solid #eaecf0;">
                    <h2 style="color: #419383; margin: 0 0 8px;">Nuevo mensaje recibido</h2>
                    <p style="color: #374151; margin: 0 0 8px;"><strong>De:</strong> %s</p>
                    <div style="background: #f9fafb; border-radius: 10px; padding: 16px; margin-top: 16px;">
                      <p style="font-size: 14px; color: #374151; font-style: italic; margin: 0;">"%s"</p>
                    </div>
                  </div>
                </div>
                """.formatted(nombreCliente, mensajeTexto);

            helper.setText(html, true);
            mailSender.send(msg);
        } catch (Exception e) {
            System.err.println("Error enviando notificación: " + e.getMessage());
        }
    }
}
