package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.WSServices.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final WhatsAppService whatsAppService;

    public void sendAppointmentConfirmationPending(Client client, LocalDateTime appointmentTime) {
        String phone = formatPhone(client.getPhone());
        String message = "Olá " + client.getName() +
                "! Barbearia The Barber recebeu seu agendamento para " +
                appointmentTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                ". Ele está *PENDENTE* e aguardando sua confirmação para garantirmos o seu atendimento. 💈 ";
        whatsAppService.sendConfirmationMessage(phone, message);
    }

    private String formatPhone(String phone) {
        String formattedPhone = phone.replaceAll("[^0-9]", "");
        if (!formattedPhone.startsWith("55")) {
            formattedPhone = "55" + formattedPhone;
        }
        return formattedPhone;
    }
}
