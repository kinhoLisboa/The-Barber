package com.theBarber.TheBarber_notification.observer;

import com.theBarber.TheBarber_notification.client.WhatsAppService;
import com.theBarber.TheBarber_notification.dto.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Log4j2
@Component
public class WhatsAppNotificationObserver implements ObserverNotification{

    private final WhatsAppService service;

    @Override
    public void notify(AppointmentEvent event) {

        String phone = event.clientPhone();

        String message = switch (event.type()) {

            case "CREATED" ->
                    "Olá " + event.clientName() +
                            ", seu agendamento foi criado com sucesso para " +
                            event.appointmentTime();

            case "CONFIRMED" ->
                    "Olá " + event.clientName() +
                            ", seu agendamento foi confirmado para " +
                            event.appointmentTime();

            case "CANCELED" ->
                    "Olá " + event.clientName() +
                            ", seu agendamento foi cancelado.";

            default ->
                    "Atualização no seu agendamento.";
        };

        log.info("🔔 Enviando WhatsApp para {}", phone);

        service.sendMessage(phone, message);
    }
}


