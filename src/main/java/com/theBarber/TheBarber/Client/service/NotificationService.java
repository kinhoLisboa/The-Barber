package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.WSServices.WhatsAppService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class NotificationService {

    private final WhatsAppService whatsappService;

    public void sendAppointmentConfirmation(Client client, LocalDateTime appointmentTime) {
        String formattedPhone = formatPhone(client.getPhone());
        String message = "✅ Olá, " + client.getName() + "! Seu agendamento foi confirmado para "
                + appointmentTime.toLocalDate() + " às " + appointmentTime.toLocalTime() + ". Nos vemos em breve! ✂️💈";

        try {
            whatsappService.sendWhatsAppMessage(formattedPhone, message);
        } catch (Exception e) {
            // Logar erro ou tratar conforme necessidade
        }
    }

    private String formatPhone(String phone) {
        String formattedPhone = phone.replaceAll("[^0-9]", "");
        if (!formattedPhone.startsWith("55")) {
            formattedPhone = "55" + formattedPhone;
        }
        return formattedPhone;
    }
}
