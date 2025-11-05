package com.theBarber.TheBarber.WSServices;

import com.theBarber.TheBarber.Client.model.Appointment;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WhatsappObserver implements  ObserverAppointment{

    private final RabbitTemplate rabbitTemplate;
    private final NotifierAppointment notifierAppointment;

    private static final String EXCHANGE = "notification.exchange";
    private static final String ROUTING_KEY = "notification.whatsapp";

    @PostConstruct
    public void init() {
        notifierAppointment.register(this);
    }

    @Override
    public void onCreated(Appointment appointment) {
        String message = String.format(
                "Olá %s! 👋\n\nSeu agendamento foi criado com sucesso! ✂️\n\n📅 Data/Horário: %s\n\nAguardamos a sua confirmação.\n\nObrigado por escolher a The Barber! 🤝",
                appointment.getClient().getName(),
                appointment.getAppointmentTime()
        );
        NotificationEvent event = new NotificationEvent(
                appointment.getClient().getName(),
                appointment.getClient().getPhone(),
                appointment.getAppointmentTime(),
                NotificationEvent.NotificationType.CREATED,
                message
        );
        System.out.println("📤 Enviando mensagem para o RabbitMQ: " + event);
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }

    @Override
    public void onConfirmed(Appointment appointment) {
        String message = String.format(
                "✅ Olá %s! Seu agendamento está CONFIRMADO!\n\n📅 Data/Horário: %s\n\nEsperamos você na The Barber. 😉",
                appointment.getClient().getName(),
                appointment.getAppointmentTime()
        );
        NotificationEvent event = new NotificationEvent(
                appointment.getClient().getName(),
                appointment.getClient().getPhone(),
                appointment.getAppointmentTime(),
                NotificationEvent.NotificationType.CONFIRMED,
                message
        );
        rabbitTemplate.convertAndSend(EXCHANGE, ROUTING_KEY, event);
    }



}
