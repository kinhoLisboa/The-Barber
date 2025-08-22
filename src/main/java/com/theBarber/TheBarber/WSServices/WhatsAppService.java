package com.theBarber.TheBarber.WSServices;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
@Log4j2
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final WhatsAppClient whatsappClient;

    @Value("${zapi.instance-id}")
    private String instanceId;

    @Value("${zapi.token}")
    private String token;

    @Value("${zapi.account-token}")
    private String accountToken;

    public void sendConfirmationMessage(String phone, String message) {
        System.out.println("Enviando mensagem para: " + phone);
        System.out.println("Conteúdo: " + message);
        WhatsAppMessageRequest request = new WhatsAppMessageRequest(phone, message);
        whatsappClient.sendMessage(instanceId, token, request, accountToken);
    }
}

