package com.theBarber.TheBarber.WSServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


import java.util.Map;
@Log4j2
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final WhatsAppClient whatsAppClient;
    private final String TOKEN = "2381E4A2AE4DA5D7791337D1";

    public void sendWhatsAppMessage(String phone, String message) {
        // Remover caracteres não numéricos do número do telefone
        String formattedPhone = phone.replaceAll("[^0-9]", "");
        if (!formattedPhone.startsWith("55")) {
            formattedPhone = "55" + formattedPhone; // Adiciona DDI do Brasil se não tiver
        }

        Map<String, String> requestBody = Map.of(
                "phone", formattedPhone,
                "message", message
        );

        try {
            ResponseEntity<String> response = whatsAppClient.sendWhatsAppMessage(TOKEN, requestBody);
            log.info("Resposta da API: {}", response.getBody());
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para {}: {}", phone, e.getMessage(), e);
        }
    }
}

