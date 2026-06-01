package com.theBarber.TheBarber.event.publisher;

import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.event.enums.AppointmentEventType;
import com.theBarber.TheBarber.event.model.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class AppointmentEventPublisher {


    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE = "notification.exchange";
    private static final String ROUTING_KEY = "notification.whatsapp";

    public void publish(AppointmentEvent event) {
        try {
            log.info("📤 Publicando evento: {}", event);

            rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);

        } catch (Exception e) {
            log.error("❌ Erro ao publicar evento: {}", event, e);
            throw e; // importante não engolir erro
        }
    }

    public void publishCreated(Appointment appointment) {

        AppointmentEvent event = new AppointmentEvent(
                UUID.randomUUID(), // 🔥 id do evento
                AppointmentEventType.CREATED,
                appointment.getId(),
                appointment.getClient().getName(),
                appointment.getClient().getPhone(),
                appointment.getAppointmentTime()
        );

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
    public void publishConfirmed(Appointment appointment) {

        AppointmentEvent event = new AppointmentEvent(
                UUID.randomUUID(),
                AppointmentEventType.CONFIRMED,
                appointment.getId(),
                appointment.getClient().getName(),
                appointment.getClient().getPhone(),
                appointment.getAppointmentTime()
        );

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }

    public void publishCanceled(Appointment appointment) {

        AppointmentEvent event = new AppointmentEvent(
                UUID.randomUUID(),
                AppointmentEventType.CANCELED,
                appointment.getId(),
                appointment.getClient().getName(),
                appointment.getClient().getPhone(),
                appointment.getAppointmentTime()
        );

        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }
}
