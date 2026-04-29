package com.asturhome.backend.init;

import com.asturhome.backend.entity.Mensaje;
import com.asturhome.backend.entity.Propiedad;
import com.asturhome.backend.entity.RespuestaMensaje;
import com.asturhome.backend.entity.Role;
import com.asturhome.backend.entity.User;
import com.asturhome.backend.repository.MensajeRepository;
import com.asturhome.backend.repository.PropiedadRepository;
import com.asturhome.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired private UserRepository userRepository;
    @Autowired private PropiedadRepository propiedadRepository;
    @Autowired private MensajeRepository mensajeRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            userRepository.saveAll(List.of(
                new User(null, "adminasturhome@proton.me",  passwordEncoder.encode("admin123"),  Role.ADMIN, "Administrador"),
                new User(null, "agente1asturhome@proton.me", passwordEncoder.encode("agente123"), Role.AGENT, "Agente Uno"),
                new User(null, "agente2asturhome@proton.me", passwordEncoder.encode("agente123"), Role.AGENT, "Agente Dos")
            ));
        }

        if (mensajeRepository.count() == 0) {
            mensajeRepository.saveAll(List.of(

                // ── PRUEBA EMAIL ─────────────────────────────────────
                mensaje("Agente Dos Test", "agente2asturhome@proton.me", "+34 600 000 000", "Información", "Piso Centro Oviedo", "Compra",
                    "Hola, me interesa este piso. ¿Podríamos hablar?",
                    "nuevos", LocalDateTime.now().minusHours(1), List.of()),

                // ── NUEVOS (5) ──────────────────────────────────────
                mensaje("Alejandro Sanz", "a.sanz@email.com", "+34 612 345 678", "Información", "Ático Calle Uría", "Alquiler",
                    "Hola, estoy muy interesado en el ático de la calle Uría. ¿Podríamos concertar una visita esta semana?",
                    "nuevos", LocalDateTime.now().minusHours(2), List.of()),
                mensaje("Marta Domínguez", "m.dom@servicios.es", "+34 699 001 223", "Información", "Parcela Llanes", "Compra",
                    "Solicitud de información sobre la parcela en Llanes. ¿Tiene agua y luz disponibles?",
                    "nuevos", LocalDateTime.now().minusDays(2), List.of()),
                mensaje("Jorge Iglesias", "j.iglesias@hotmail.com", "+34 677 234 567", "Visita", "Villa Somió", "Compra",
                    "Me gustaría visitar la villa de Somió este fin de semana si es posible.",
                    "nuevos", LocalDateTime.now().minusDays(4), List.of()),
                mensaje("Ana Martínez", "ana.mtz@gmail.com", "+34 644 112 998", "Precio / Negociación", "Chalet Lugones", "Compra",
                    "¿Tienen margen de negociación en el chalet de Lugones? Mi presupuesto máximo es 280.000 €.",
                    "nuevos", LocalDateTime.now().minusDays(1), List.of()),
                mensaje("Pedro Castillo", "pcastillo@empresa.es", "+34 633 876 543", "Información", null, "Alquiler",
                    "Busco un piso de 2 habitaciones en Gijón para alquilar a partir de junio. ¿Qué tienen disponible?",
                    "nuevos", LocalDateTime.now().minusHours(5), List.of()),

                // ── EN PROCESO (5) ──────────────────────────────────
                mensaje("Carlos Ruiz", "cruiz@gmail.com", "+34 600 112 233", "Documentación", "Piso Centro Oviedo", "Alquiler",
                    "Revisando el contrato de alquiler. Hay una cláusula sobre las mascotas que me gustaría aclarar.",
                    "en_proceso", LocalDateTime.now().minusDays(3),
                    List.of(
                        burbuja("agente", "Hola Carlos, la cláusula 8.2 indica que se permiten mascotas de hasta 10 kg con depósito adicional.", LocalDateTime.now().minusDays(3).plusHours(1)),
                        burbuja("cliente", "Perfecto, mi perro pesa 8 kg. ¿Cuánto sería el depósito adicional?", LocalDateTime.now().minusDays(3).plusHours(3))
                    )),
                mensaje("Isabel Fernández", "i.fernandez@live.com", "+34 655 001 122", "Visita", "Apartamento Salinas", "Alquiler",
                    "Queremos visitar el apartamento de Salinas. Somos una familia de 4 personas.",
                    "en_proceso", LocalDateTime.now().minusDays(2),
                    List.of(
                        burbuja("agente", "Hola Isabel, podemos organizar la visita el martes a las 17:00. ¿Le viene bien?", LocalDateTime.now().minusDays(2).plusHours(2))
                    )),
                mensaje("Tomás Vega", "tvega@outlook.es", "+34 611 334 556", "Precio / Negociación", "Casa Cangas de Onís", "Compra",
                    "Estoy interesado en la casa de Cangas. ¿Aceptarían 230.000 en vez de 245.000?",
                    "en_proceso", LocalDateTime.now().minusDays(4),
                    List.of(
                        burbuja("agente", "Hola Tomás, lo consultamos con el propietario y le confirmamos en 24h.", LocalDateTime.now().minusDays(4).plusHours(1)),
                        burbuja("cliente", "De acuerdo, espero noticias. Gracias.", LocalDateTime.now().minusDays(3))
                    )),
                mensaje("Laura Blanco", "lblanco@correo.com", "+34 699 445 667", "Documentación", "Dúplex Avilés", "Compra",
                    "Necesito la nota simple del dúplex de Avilés para mi banco antes del viernes.",
                    "en_proceso", LocalDateTime.now().minusDays(1),
                    List.of(
                        burbuja("agente", "Hola Laura, estamos solicitándola al registro. En 48h la tendrá.", LocalDateTime.now().minusDays(1).plusHours(2))
                    )),
                mensaje("Marcos Prieto", "m.prieto@gmail.com", "+34 622 789 012", "Información", "Piso Naranco Oviedo", "Compra",
                    "¿El piso del Naranco tiene plaza de garaje incluida en el precio?",
                    "en_proceso", LocalDateTime.now().minusDays(2),
                    List.of(
                        burbuja("agente", "Hola Marcos, sí, incluye una plaza de garaje en el sótano del edificio.", LocalDateTime.now().minusDays(2).plusHours(1)),
                        burbuja("cliente", "Perfecto. ¿Podría enviarme más fotos del garaje y la cocina?", LocalDateTime.now().minusDays(1))
                    )),

                // ── RESPONDIDOS (5) ─────────────────────────────────
                mensaje("Lucía Fernández", "lucia.fdez@live.com", "+34 655 443 322", "Visita", null, "Compra",
                    "Buenos días, me gustaría concertar una visita para ver algún piso de 3 habitaciones en Oviedo.",
                    "respondidos", LocalDateTime.now().minusDays(5),
                    List.of(
                        burbuja("agente", "Hola Lucía, tenemos varias opciones. Le envío los planos ahora mismo.", LocalDateTime.now().minusDays(5).plusHours(1)),
                        burbuja("cliente", "Gracias, los revisaré con mi arquitecto y te comento la semana que viene.", LocalDateTime.now().minusDays(4))
                    )),
                mensaje("Rosa Herrero", "r.herrero@icloud.com", "+34 644 223 334", "Precio / Negociación", "Ático Gijón Mar", "Compra",
                    "¿El precio del ático incluye los muebles de cocina? Son de muy buena calidad.",
                    "respondidos", LocalDateTime.now().minusDays(6),
                    List.of(
                        burbuja("agente", "Hola Rosa, sí, la cocina completa está incluida en el precio de venta.", LocalDateTime.now().minusDays(6).plusHours(2)),
                        burbuja("cliente", "Estupendo, voy a comentárselo a mi pareja y os confirmo esta semana.", LocalDateTime.now().minusDays(5))
                    )),
                mensaje("David Moreno", "d.moreno@yahoo.es", "+34 677 556 889", "Documentación", "Casa Siero", "Compra",
                    "¿Tienen la cédula de habitabilidad de la casa de Siero actualizada?",
                    "respondidos", LocalDateTime.now().minusDays(7),
                    List.of(
                        burbuja("agente", "Hola David, sí, la cédula está vigente hasta 2028. Se la enviamos por email.", LocalDateTime.now().minusDays(7).plusHours(1))
                    )),
                mensaje("Elena Torres", "e.torres@gmail.com", "+34 611 998 776", "Visita", "Chalet Cabo Peñas", "Compra",
                    "Nos ha encantado el chalet de Cabo Peñas en las fotos. ¿Cuándo podemos visitarlo?",
                    "respondidos", LocalDateTime.now().minusDays(4),
                    List.of(
                        burbuja("agente", "Hola Elena, tenemos disponibilidad el sábado a las 11:00 o el domingo a las 12:00. ¿Cuál prefieren?", LocalDateTime.now().minusDays(4).plusHours(3)),
                        burbuja("cliente", "El sábado a las 11 nos viene perfecto. ¡Hasta entonces!", LocalDateTime.now().minusDays(3))
                    )),
                mensaje("Fernando Crespo", "fcrespo@empresa.com", "+34 633 112 445", "Información", "Finca Valdés", "Compra",
                    "Me interesa la finca de Valdés para uso agrícola. ¿Tiene acceso por carretera asfaltada?",
                    "respondidos", LocalDateTime.now().minusDays(8),
                    List.of(
                        burbuja("agente", "Hola Fernando, sí, hay acceso por carretera asfaltada hasta la entrada de la finca.", LocalDateTime.now().minusDays(8).plusHours(2))
                    )),

                // ── CERRADOS (5) ────────────────────────────────────
                mensaje("Roberto M.", "roberto.g@outlook.es", "+34 688 776 655", "Precio / Negociación", "Chalet Gijón", "Compra",
                    "¿Hay margen de negociación en el precio del chalet de Gijón?",
                    "cerrados", LocalDateTime.now().minusDays(10),
                    List.of(
                        burbuja("agente", "Hola Roberto, el propietario acepta hasta un 3% de descuento. Precio final: 387.000 €.", LocalDateTime.now().minusDays(10).plusHours(1)),
                        burbuja("cliente", "Aceptado. ¿Cuándo firmamos las arras?", LocalDateTime.now().minusDays(10).plusHours(2)),
                        burbuja("agente", "Operación cerrada. Firma el jueves a las 12:00 en notaría.", LocalDateTime.now().minusDays(9))
                    )),
                mensaje("Sonia Peña", "sonia@gestoria.com", "+34 633 221 100", "Documentación", "Apartamento Salinas", "Venta",
                    "El comprador no ha conseguido la hipoteca. La operación queda cancelada.",
                    "cerrados", LocalDateTime.now().minusDays(12),
                    List.of(
                        burbuja("agente", "Entendido Sonia. Volvemos a activar el anuncio del apartamento de Salinas.", LocalDateTime.now().minusDays(12).plusHours(1))
                    )),
                mensaje("Carmen Iglesias", "c.iglesias@hotmail.com", "+34 644 334 556", "Visita", "Piso Casco Antiguo Oviedo", "Alquiler",
                    "Visitamos el piso y nos ha gustado mucho. Queremos alquilarlo.",
                    "cerrados", LocalDateTime.now().minusDays(15),
                    List.of(
                        burbuja("agente", "Estupendo Carmen. Contrato firmado, entrega de llaves el día 1 del próximo mes.", LocalDateTime.now().minusDays(14))
                    )),
                mensaje("Raúl Mendoza", "r.mendoza@gmail.com", "+34 622 445 778", "Precio / Negociación", "Casa Villaviciosa", "Compra",
                    "Hemos llegado a un acuerdo con el precio. ¿Cuáles son los próximos pasos?",
                    "cerrados", LocalDateTime.now().minusDays(20),
                    List.of(
                        burbuja("agente", "Perfecto Raúl. El siguiente paso es firmar el contrato de arras y pagar el 10% de señal.", LocalDateTime.now().minusDays(20).plusHours(1)),
                        burbuja("cliente", "De acuerdo. Nos ponemos en contacto con el notario esta semana.", LocalDateTime.now().minusDays(19)),
                        burbuja("agente", "Operación completada. Escritura firmada. ¡Enhorabuena por su nueva casa!", LocalDateTime.now().minusDays(10))
                    )),
                mensaje("Patricia Suárez", "p.suarez@correo.es", "+34 655 667 889", "Información", "Estudio Oviedo Centro", "Alquiler",
                    "Ya no necesito el estudio, he encontrado otro piso. Disculpen las molestias.",
                    "cerrados", LocalDateTime.now().minusDays(8),
                    List.of(
                        burbuja("agente", "Sin problema Patricia, quedamos a su disposición para futuras necesidades.", LocalDateTime.now().minusDays(8).plusHours(1))
                    ))
            ));
        }

        if (propiedadRepository.count() == 0) {
            LocalDateTime now = LocalDateTime.now();
            propiedadRepository.saveAll(List.of(

                prop("Ático de Lujo en el Centro", "Piso", "VENTA", "disponible", "Oviedo", 345000.0, 3, 2, 110.0, now.minusDays(2), List.of("https://picsum.photos/seed/p1a/800/450","https://picsum.photos/seed/p1b/800/450","https://picsum.photos/seed/p1c/800/450")),
                prop("Chalet Independiente Somió", "Chalet", "VENTA", "reservado", "Gijón", 780000.0, 5, 3, 320.0, now.minusDays(5), List.of("https://picsum.photos/seed/p2a/800/450","https://picsum.photos/seed/p2b/800/450")),
                prop("Estudio Moderno La Arena", "Apartamento", "ALQUILER", "alquilado", "Gijón", 850.0, 1, 1, 42.0, now.minusDays(1), List.of("https://picsum.photos/seed/p3a/800/450","https://picsum.photos/seed/p3b/800/450")),
                prop("Casa Rural Reformada El Campu", "Casa", "VENTA", "vendido", "Avilés", 195000.0, 4, 2, 185.0, now.minusMonths(1).minusDays(3), List.of("https://picsum.photos/seed/p4a/800/450","https://picsum.photos/seed/p4b/800/450","https://picsum.photos/seed/p4c/800/450")),
                prop("Piso Reformado Casco Antiguo", "Piso", "ALQUILER", "disponible", "Oviedo", 1100.0, 2, 1, 75.0, now.minusDays(3), List.of("https://picsum.photos/seed/p5a/800/450","https://picsum.photos/seed/p5b/800/450")),
                prop("Villa con Vistas al Mar Candás", "Chalet", "VENTA", "disponible", "Carreño", 620000.0, 4, 3, 280.0, now.minusMonths(1).minusDays(8), List.of("https://picsum.photos/seed/p6a/800/450","https://picsum.photos/seed/p6b/800/450","https://picsum.photos/seed/p6c/800/450")),
                prop("Apartamento Frente al Mar Salinas", "Apartamento", "ALQUILER", "disponible", "Avilés", 950.0, 2, 1, 65.0, now.minusMonths(1).minusDays(12), List.of("https://picsum.photos/seed/p7a/800/450","https://picsum.photos/seed/p7b/800/450")),
                prop("Casa de Aldea Restaurada Pravia", "Casa", "VENTA", "disponible", "Pravia", 165000.0, 3, 2, 160.0, now.minusMonths(1).minusDays(20), List.of("https://picsum.photos/seed/p8a/800/450","https://picsum.photos/seed/p8b/800/450")),
                prop("Piso Luminoso Zona El Naranco", "Piso", "VENTA", "disponible", "Oviedo", 210000.0, 3, 2, 95.0, now.minusMonths(2).minusDays(2), List.of("https://picsum.photos/seed/p9a/800/450","https://picsum.photos/seed/p9b/800/450","https://picsum.photos/seed/p9c/800/450")),
                prop("Chalet Adosado Urbanización Navia", "Chalet", "VENTA", "reservado", "Navia", 285000.0, 3, 2, 140.0, now.minusMonths(2).minusDays(5), List.of("https://picsum.photos/seed/p10a/800/450","https://picsum.photos/seed/p10b/800/450")),
                prop("Apartamento Llanes Casco Histórico", "Apartamento", "ALQUILER", "alquilado", "Llanes", 700.0, 1, 1, 48.0, now.minusMonths(2).minusDays(10), List.of("https://picsum.photos/seed/p11a/800/450","https://picsum.photos/seed/p11b/800/450")),
                prop("Finca con Hórreo Comarca Aller", "Casa", "VENTA", "disponible", "Aller", 130000.0, 4, 2, 220.0, now.minusMonths(2).minusDays(15), List.of("https://picsum.photos/seed/p12a/800/450","https://picsum.photos/seed/p12b/800/450","https://picsum.photos/seed/p12c/800/450")),
                prop("Piso Zona Puerto Deportivo Gijón", "Piso", "VENTA", "disponible", "Gijón", 185000.0, 2, 1, 80.0, now.minusMonths(2).minusDays(18), List.of("https://picsum.photos/seed/p13a/800/450","https://picsum.photos/seed/p13b/800/450")),
                prop("Cabaña de Montaña Somiedo", "Casa", "VENTA", "disponible", "Somiedo", 98000.0, 2, 1, 90.0, now.minusMonths(3).minusDays(1), List.of("https://picsum.photos/seed/p14a/800/450","https://picsum.photos/seed/p14b/800/450")),
                prop("Dúplex Reformado Mieres Centro", "Piso", "ALQUILER", "disponible", "Mieres", 650.0, 3, 2, 100.0, now.minusMonths(3).minusDays(4), List.of("https://picsum.photos/seed/p15a/800/450","https://picsum.photos/seed/p15b/800/450")),
                prop("Villa Lujo con Piscina Gijón", "Chalet", "VENTA", "disponible", "Gijón", 950000.0, 6, 4, 420.0, now.minusMonths(3).minusDays(9), List.of("https://picsum.photos/seed/p16a/800/450","https://picsum.photos/seed/p16b/800/450","https://picsum.photos/seed/p16c/800/450")),
                prop("Piso Céntrico Avilés Reformado", "Piso", "ALQUILER", "alquilado", "Avilés", 580.0, 2, 1, 70.0, now.minusMonths(3).minusDays(14), List.of("https://picsum.photos/seed/p17a/800/450","https://picsum.photos/seed/p17b/800/450")),
                prop("Casa con Terreno Cangas de Onís", "Casa", "VENTA", "disponible", "Cangas de Onís", 245000.0, 4, 2, 200.0, now.minusMonths(3).minusDays(20), List.of("https://picsum.photos/seed/p18a/800/450","https://picsum.photos/seed/p18b/800/450","https://picsum.photos/seed/p18c/800/450")),
                prop("Estudio Amueblado Oviedo Centro", "Apartamento", "ALQUILER", "disponible", "Oviedo", 550.0, 1, 1, 38.0, now.minusMonths(4).minusDays(2), List.of("https://picsum.photos/seed/p19a/800/450","https://picsum.photos/seed/p19b/800/450")),
                prop("Chalet con Jardín Lugones", "Chalet", "VENTA", "vendido", "Siero", 340000.0, 4, 3, 190.0, now.minusMonths(4).minusDays(6), List.of("https://picsum.photos/seed/p20a/800/450","https://picsum.photos/seed/p20b/800/450")),
                prop("Piso Vistas al Mar Luanco", "Piso", "VENTA", "disponible", "Gozón", 225000.0, 3, 2, 88.0, now.minusMonths(4).minusDays(11), List.of("https://picsum.photos/seed/p21a/800/450","https://picsum.photos/seed/p21b/800/450","https://picsum.photos/seed/p21c/800/450")),
                prop("Casa Pueblo Tineo Reformada", "Casa", "VENTA", "disponible", "Tineo", 88000.0, 3, 1, 140.0, now.minusMonths(4).minusDays(16), List.of("https://picsum.photos/seed/p22a/800/450","https://picsum.photos/seed/p22b/800/450")),
                prop("Apartamento Playa de Salinas", "Apartamento", "ALQUILER", "reservado", "Avilés", 1200.0, 2, 1, 60.0, now.minusMonths(4).minusDays(22), List.of("https://picsum.photos/seed/p23a/800/450","https://picsum.photos/seed/p23b/800/450")),
                prop("Bajo con Jardín Privado Oviedo", "Piso", "VENTA", "disponible", "Oviedo", 195000.0, 3, 2, 105.0, now.minusMonths(5).minusDays(1), List.of("https://picsum.photos/seed/p24a/800/450","https://picsum.photos/seed/p24b/800/450")),
                prop("Palloza Restaurada Ibias", "Casa", "VENTA", "disponible", "Ibias", 75000.0, 2, 1, 110.0, now.minusMonths(5).minusDays(5), List.of("https://picsum.photos/seed/p25a/800/450","https://picsum.photos/seed/p25b/800/450","https://picsum.photos/seed/p25c/800/450")),
                prop("Piso Amplio Barrio La Calzada", "Piso", "ALQUILER", "alquilado", "Gijón", 620.0, 3, 1, 90.0, now.minusMonths(5).minusDays(10), List.of("https://picsum.photos/seed/p26a/800/450","https://picsum.photos/seed/p26b/800/450")),
                prop("Casa con Cuadra Grandas de Salime", "Casa", "VENTA", "disponible", "Grandas de Salime", 55000.0, 3, 1, 180.0, now.minusMonths(5).minusDays(15), List.of("https://picsum.photos/seed/p27a/800/450","https://picsum.photos/seed/p27b/800/450")),
                prop("Chalet Pareado Llanera", "Chalet", "VENTA", "reservado", "Llanera", 275000.0, 3, 2, 155.0, now.minusMonths(5).minusDays(20), List.of("https://picsum.photos/seed/p28a/800/450","https://picsum.photos/seed/p28b/800/450","https://picsum.photos/seed/p28c/800/450")),
                prop("Ático con Terraza Gijón El Molinón", "Piso", "ALQUILER", "disponible", "Gijón", 900.0, 2, 1, 75.0, now.minusDays(4), List.of("https://picsum.photos/seed/p29a/800/450","https://picsum.photos/seed/p29b/800/450")),
                prop("Casa Rural Turismo Taramundi", "Casa", "VENTA", "disponible", "Taramundi", 145000.0, 5, 3, 260.0, now.minusMonths(1).minusDays(5), List.of("https://picsum.photos/seed/p30a/800/450","https://picsum.photos/seed/p30b/800/450","https://picsum.photos/seed/p30c/800/450")),
                prop("Piso Nuevo Promoción Oviedo Sur", "Piso", "VENTA", "disponible", "Oviedo", 265000.0, 3, 2, 98.0, now.minusMonths(1).minusDays(15), List.of("https://picsum.photos/seed/p31a/800/450","https://picsum.photos/seed/p31b/800/450")),
                prop("Apartamento Costa Verde Cudillero", "Apartamento", "ALQUILER", "disponible", "Cudillero", 750.0, 1, 1, 45.0, now.minusMonths(2).minusDays(7), List.of("https://picsum.photos/seed/p32a/800/450","https://picsum.photos/seed/p32b/800/450")),
                prop("Casa Señorial Casco Histórico Oviedo", "Casa", "VENTA", "disponible", "Oviedo", 580000.0, 6, 4, 380.0, now.minusMonths(2).minusDays(22), List.of("https://picsum.photos/seed/p33a/800/450","https://picsum.photos/seed/p33b/800/450","https://picsum.photos/seed/p33c/800/450")),
                prop("Piso Reformado Langreo Centro", "Piso", "ALQUILER", "alquilado", "Langreo", 480.0, 2, 1, 68.0, now.minusMonths(3).minusDays(7), List.of("https://picsum.photos/seed/p34a/800/450","https://picsum.photos/seed/p34b/800/450")),
                prop("Chalet Independiente Cabo Peñas", "Chalet", "VENTA", "disponible", "Gozón", 490000.0, 4, 3, 240.0, now.minusMonths(3).minusDays(17), List.of("https://picsum.photos/seed/p35a/800/450","https://picsum.photos/seed/p35b/800/450","https://picsum.photos/seed/p35c/800/450")),
                prop("Estudio Zona Universidad Oviedo", "Apartamento", "ALQUILER", "alquilado", "Oviedo", 500.0, 1, 1, 35.0, now.minusMonths(4).minusDays(3), List.of("https://picsum.photos/seed/p36a/800/450","https://picsum.photos/seed/p36b/800/450")),
                prop("Caserío Reformado Villaviciosa", "Casa", "VENTA", "disponible", "Villaviciosa", 310000.0, 5, 3, 295.0, now.minusMonths(4).minusDays(19), List.of("https://picsum.photos/seed/p37a/800/450","https://picsum.photos/seed/p37b/800/450")),
                prop("Piso Con Parking Avilés Norte", "Piso", "VENTA", "vendido", "Avilés", 142000.0, 3, 1, 82.0, now.minusMonths(5).minusDays(3), List.of("https://picsum.photos/seed/p38a/800/450","https://picsum.photos/seed/p38b/800/450")),
                prop("Bungalow Playa España Ribadesella", "Apartamento", "ALQUILER", "disponible", "Ribadesella", 1100.0, 2, 1, 58.0, now.minusMonths(5).minusDays(8), List.of("https://picsum.photos/seed/p39a/800/450","https://picsum.photos/seed/p39b/800/450","https://picsum.photos/seed/p39c/800/450")),
                prop("Casa con Bodega Cangas del Narcea", "Casa", "VENTA", "disponible", "Cangas del Narcea", 115000.0, 4, 2, 175.0, now.minusMonths(5).minusDays(25), List.of("https://picsum.photos/seed/p40a/800/450","https://picsum.photos/seed/p40b/800/450")),
                prop("Piso Zona Cimadevilla Gijón", "Piso", "ALQUILER", "reservado", "Gijón", 750.0, 2, 1, 72.0, now.minusDays(6), List.of("https://picsum.photos/seed/p41a/800/450","https://picsum.photos/seed/p41b/800/450")),
                prop("Villa Modernista Campo de Caso", "Chalet", "VENTA", "disponible", "Caso", 385000.0, 4, 3, 210.0, now.minusMonths(1).minusDays(18), List.of("https://picsum.photos/seed/p42a/800/450","https://picsum.photos/seed/p42b/800/450","https://picsum.photos/seed/p42c/800/450")),
                prop("Dúplex Vistas Ría Avilés", "Piso", "VENTA", "disponible", "Avilés", 178000.0, 3, 2, 112.0, now.minusMonths(2).minusDays(25), List.of("https://picsum.photos/seed/p43a/800/450","https://picsum.photos/seed/p43b/800/450")),
                prop("Apartamento Turístico Covadonga", "Apartamento", "ALQUILER", "alquilado", "Cangas de Onís", 850.0, 2, 1, 55.0, now.minusMonths(3).minusDays(25), List.of("https://picsum.photos/seed/p44a/800/450","https://picsum.photos/seed/p44b/800/450")),
                prop("Casa con Piscina Siero", "Casa", "VENTA", "disponible", "Siero", 420000.0, 5, 3, 300.0, now.minusMonths(4).minusDays(8), List.of("https://picsum.photos/seed/p45a/800/450","https://picsum.photos/seed/p45b/800/450","https://picsum.photos/seed/p45c/800/450")),
                prop("Piso Antiguo Reformado Oviedo", "Piso", "VENTA", "vendido", "Oviedo", 155000.0, 2, 1, 78.0, now.minusMonths(4).minusDays(24), List.of("https://picsum.photos/seed/p46a/800/450","https://picsum.photos/seed/p46b/800/450")),
                prop("Chalet Esquina Urbanización Gijón", "Chalet", "ALQUILER", "disponible", "Gijón", 1800.0, 4, 2, 200.0, now.minusMonths(5).minusDays(12), List.of("https://picsum.photos/seed/p47a/800/450","https://picsum.photos/seed/p47b/800/450")),
                prop("Bajo Comercial Reconvertido Loft", "Apartamento", "ALQUILER", "disponible", "Oviedo", 720.0, 1, 1, 62.0, now.minusMonths(5).minusDays(18), List.of("https://picsum.photos/seed/p48a/800/450","https://picsum.photos/seed/p48b/800/450","https://picsum.photos/seed/p48c/800/450")),
                prop("Finca Ganadera Reconvertida Valdés", "Casa", "VENTA", "disponible", "Valdés", 195000.0, 4, 2, 350.0, now.minusDays(7), List.of("https://picsum.photos/seed/p49a/800/450","https://picsum.photos/seed/p49b/800/450")),
                prop("Penthouse con Terraza Gijón Mar", "Piso", "VENTA", "reservado", "Gijón", 430000.0, 3, 2, 130.0, now.minusMonths(1).minusDays(25), List.of("https://picsum.photos/seed/p50a/800/450","https://picsum.photos/seed/p50b/800/450","https://picsum.photos/seed/p50c/800/450"))
            ));
        }

        // Propiedades meses 6-11 + nuevos tipos (Local, Terreno, Garaje)
        if (propiedadRepository.count() <= 50) {
            LocalDateTime now = LocalDateTime.now();
            propiedadRepository.saveAll(List.of(
                // ── Mayo 2025 (mes 11) ──────────────────────────────
                prop("Local Comercial Calle Uría Oviedo", "Local", "VENTA", "disponible", "Oviedo", 195000.0, 0, 1, 88.0, now.minusMonths(11).minusDays(4), List.of("https://picsum.photos/seed/p51a/800/450","https://picsum.photos/seed/p51b/800/450")),
                prop("Terreno Edificable Gijón Sur", "Terreno", "VENTA", "disponible", "Gijón", 78000.0, 0, 0, 350.0, now.minusMonths(11).minusDays(10), List.of("https://picsum.photos/seed/p52a/800/450","https://picsum.photos/seed/p52b/800/450")),
                prop("Garaje Doble Centro Oviedo", "Garaje", "VENTA", "vendido", "Oviedo", 24000.0, 0, 0, 28.0, now.minusMonths(11).minusDays(18), List.of("https://picsum.photos/seed/p53a/800/450")),
                prop("Piso Familiar Lugones", "Piso", "ALQUILER", "alquilado", "Siero", 720.0, 3, 1, 88.0, now.minusMonths(11).minusDays(22), List.of("https://picsum.photos/seed/p54a/800/450","https://picsum.photos/seed/p54b/800/450")),

                // ── Junio 2025 (mes 10) ─────────────────────────────
                prop("Terreno con Proyecto Oviedo Este", "Terreno", "VENTA", "disponible", "Oviedo", 135000.0, 0, 0, 480.0, now.minusMonths(10).minusDays(3), List.of("https://picsum.photos/seed/p55a/800/450","https://picsum.photos/seed/p55b/800/450")),
                prop("Local Hostelería Puerto Gijón", "Local", "ALQUILER", "alquilado", "Gijón", 2200.0, 0, 1, 120.0, now.minusMonths(10).minusDays(9), List.of("https://picsum.photos/seed/p56a/800/450","https://picsum.photos/seed/p56b/800/450")),
                prop("Piso Reformado Zona Pescadores Gijón", "Piso", "VENTA", "disponible", "Gijón", 215000.0, 2, 1, 78.0, now.minusMonths(10).minusDays(15), List.of("https://picsum.photos/seed/p57a/800/450","https://picsum.photos/seed/p57b/800/450","https://picsum.photos/seed/p57c/800/450")),
                prop("Garaje Cubierto Avilés Centro", "Garaje", "ALQUILER", "disponible", "Avilés", 90.0, 0, 0, 14.0, now.minusMonths(10).minusDays(20), List.of("https://picsum.photos/seed/p58a/800/450")),

                // ── Julio 2025 (mes 9) ──────────────────────────────
                prop("Local Industrial Polígono Avilés", "Local", "VENTA", "disponible", "Avilés", 320000.0, 0, 2, 250.0, now.minusMonths(9).minusDays(5), List.of("https://picsum.photos/seed/p59a/800/450","https://picsum.photos/seed/p59b/800/450")),
                prop("Casa Pareada Luanco Frente al Mar", "Casa", "VENTA", "reservado", "Gozón", 285000.0, 3, 2, 165.0, now.minusMonths(9).minusDays(11), List.of("https://picsum.photos/seed/p60a/800/450","https://picsum.photos/seed/p60b/800/450","https://picsum.photos/seed/p60c/800/450")),
                prop("Garaje Plaza Sencilla Gijón", "Garaje", "VENTA", "disponible", "Gijón", 18000.0, 0, 0, 12.0, now.minusMonths(9).minusDays(16), List.of("https://picsum.photos/seed/p61a/800/450")),
                prop("Apartamento Vacacional Lastres", "Apartamento", "ALQUILER", "disponible", "Colunga", 980.0, 2, 1, 52.0, now.minusMonths(9).minusDays(23), List.of("https://picsum.photos/seed/p62a/800/450","https://picsum.photos/seed/p62b/800/450")),

                // ── Agosto 2025 (mes 8) ─────────────────────────────
                prop("Terreno Rústico Cangas de Onís", "Terreno", "VENTA", "disponible", "Cangas de Onís", 45000.0, 0, 0, 1200.0, now.minusMonths(8).minusDays(4), List.of("https://picsum.photos/seed/p63a/800/450","https://picsum.photos/seed/p63b/800/450")),
                prop("Plaza de Garaje Oviedo Zona Centro", "Garaje", "VENTA", "vendido", "Oviedo", 22000.0, 0, 0, 12.0, now.minusMonths(8).minusDays(9), List.of("https://picsum.photos/seed/p64a/800/450")),
                prop("Piso Familiar con Terraza Siero", "Piso", "VENTA", "disponible", "Siero", 198000.0, 3, 2, 105.0, now.minusMonths(8).minusDays(14), List.of("https://picsum.photos/seed/p65a/800/450","https://picsum.photos/seed/p65b/800/450","https://picsum.photos/seed/p65c/800/450")),
                prop("Local con Escaparates Avilés", "Local", "ALQUILER", "disponible", "Avilés", 850.0, 0, 1, 65.0, now.minusMonths(8).minusDays(21), List.of("https://picsum.photos/seed/p66a/800/450","https://picsum.photos/seed/p66b/800/450")),

                // ── Septiembre 2025 (mes 7) ─────────────────────────
                prop("Local Céntrico Calle Corrida Gijón", "Local", "ALQUILER", "alquilado", "Gijón", 1400.0, 0, 1, 95.0, now.minusMonths(7).minusDays(6), List.of("https://picsum.photos/seed/p67a/800/450","https://picsum.photos/seed/p67b/800/450")),
                prop("Chalet Moderno Urbanización Oviedo", "Chalet", "VENTA", "disponible", "Oviedo", 420000.0, 4, 3, 220.0, now.minusMonths(7).minusDays(12), List.of("https://picsum.photos/seed/p68a/800/450","https://picsum.photos/seed/p68b/800/450","https://picsum.photos/seed/p68c/800/450")),
                prop("Terreno Parcela Edificable Llanera", "Terreno", "VENTA", "disponible", "Llanera", 62000.0, 0, 0, 420.0, now.minusMonths(7).minusDays(19), List.of("https://picsum.photos/seed/p69a/800/450","https://picsum.photos/seed/p69b/800/450")),
                prop("Apartamento Playa Llanes Verano", "Apartamento", "ALQUILER", "alquilado", "Llanes", 1350.0, 2, 1, 58.0, now.minusMonths(7).minusDays(25), List.of("https://picsum.photos/seed/p70a/800/450","https://picsum.photos/seed/p70b/800/450")),

                // ── Octubre 2025 (mes 6) ────────────────────────────
                prop("Local Comercial Esquina Oviedo", "Local", "VENTA", "disponible", "Oviedo", 245000.0, 0, 1, 115.0, now.minusMonths(6).minusDays(3), List.of("https://picsum.photos/seed/p71a/800/450","https://picsum.photos/seed/p71b/800/450")),
                prop("Terreno Urbanizable Costa Gijón", "Terreno", "VENTA", "disponible", "Gijón", 95000.0, 0, 0, 600.0, now.minusMonths(6).minusDays(8), List.of("https://picsum.photos/seed/p72a/800/450","https://picsum.photos/seed/p72b/800/450")),
                prop("Piso Vistas Ría Avilés Reformado", "Piso", "ALQUILER", "disponible", "Avilés", 680.0, 2, 1, 72.0, now.minusMonths(6).minusDays(16), List.of("https://picsum.photos/seed/p73a/800/450","https://picsum.photos/seed/p73b/800/450","https://picsum.photos/seed/p73c/800/450")),
                prop("Garaje Trastero Gijón El Coto", "Garaje", "ALQUILER", "disponible", "Gijón", 110.0, 0, 0, 16.0, now.minusMonths(6).minusDays(22), List.of("https://picsum.photos/seed/p74a/800/450"))
            ));
        }

        // Backfill vistas
        if (propiedadRepository.findAll().stream().mapToLong(Propiedad::getVistas).sum() == 0) {
            int[] vistasSeed = {
                4812,3501,2990,1850,4200,3100,2300,1650,3800,2750,
                1900,1420,3200,2600,1750,4500,1300,2100,1600,3900,
                2400,1100,1450,3300,2050,1200,2800,3600,1950,4100,
                2700,1500,3700,2200,4300,1350,3000,1700,2500,1250,
                4600,3400,2050,1800,4000,2350,1550,1400,3150,4900,
                850,620,1100,740,960,1800,730,540,
                1650,2200,480,890,1320,670,1050,930,
                1500,2800,580,1200,2100,1400,810,460
            };
            List<Propiedad> todas = propiedadRepository.findAll();
            for (int i = 0; i < todas.size(); i++) {
                long v = i < vistasSeed.length ? vistasSeed[i] : (100 + i * 29L);
                todas.get(i).setVistas(v);
            }
            propiedadRepository.saveAll(todas);
        }

        // Backfill fechaAlta
        List<Propiedad> sinFecha = propiedadRepository.findAll().stream()
            .filter(p -> p.getFechaAlta() == null)
            .collect(Collectors.toList());
        if (!sinFecha.isEmpty()) {
            LocalDateTime now2 = LocalDateTime.now();
            int[] offsets = {2,5,1,33,3,38,42,50,62,65,71,75,78,91,94,99,103,110,122,126,131,135,145,151,155,162,165,170,4,35,45,67,82,97,107,124,139,153,158,168,6,48,85,115,128,143,7,148,157,155};
            for (int i = 0; i < sinFecha.size(); i++) {
                int offset = i < offsets.length ? offsets[i] : (i * 3 + 1);
                sinFecha.get(i).setFechaAlta(now2.minusDays(offset % 365));
            }
            propiedadRepository.saveAll(sinFecha);
        }
    }

    private Mensaje mensaje(String nombre, String email, String telefono, String tipoConsulta,
                             String propiedad, String modalidad, String mensajeOriginal,
                             String estado, LocalDateTime fecha, List<RespuestaMensaje> respuestas) {
        Mensaje m = new Mensaje();
        m.setNombre(nombre);
        m.setEmail(email);
        m.setTelefono(telefono);
        m.setTipoConsulta(tipoConsulta);
        m.setPropiedad(propiedad);
        m.setModalidad(modalidad);
        m.setMensajeOriginal(mensajeOriginal);
        m.setEstado(estado);
        m.setFecha(fecha);
        m.setTokenRespuesta(UUID.randomUUID().toString());

        RespuestaMensaje primera = new RespuestaMensaje();
        primera.setMensaje(m);
        primera.setTipo("cliente");
        primera.setTexto(mensajeOriginal);
        primera.setFecha(fecha);
        m.getHilo().add(primera);

        for (RespuestaMensaje r : respuestas) {
            r.setMensaje(m);
            m.getHilo().add(r);
        }
        return m;
    }

    private RespuestaMensaje burbuja(String tipo, String texto, LocalDateTime fecha) {
        RespuestaMensaje r = new RespuestaMensaje();
        r.setTipo(tipo);
        r.setTexto(texto);
        r.setFecha(fecha);
        return r;
    }

    private Propiedad prop(String nombre, String tipo, String modalidad, String estado,
                           String ubicacion, Double precio, int hab, int banos, double sup,
                           LocalDateTime fechaAlta, List<String> imgs) {
        Propiedad p = new Propiedad();
        p.setNombre(nombre);
        p.setTipo(tipo);
        p.setModalidad(modalidad);
        p.setEstado(estado);
        p.setUbicacion(ubicacion);
        p.setPrecio(precio);
        p.setHabitaciones(hab);
        p.setBanos(banos);
        p.setSuperficie(sup);
        p.setFechaAlta(fechaAlta);
        p.setImagenes(imgs);
        return p;
    }
}
