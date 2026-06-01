package com.theBarber.TheBarber_notification.client;

import com.theBarber.TheBarber_notification.config.ZapiProperties;
import com.theBarber.TheBarber_notification.dto.ZapiRequest;
import com.theBarber.TheBarber_notification.exception.TechnicalException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
@Log4j2
@Service
@RequiredArgsConstructor
public class WhatsAppService {

    private final WhatsAppClient whatsappClient;
    private final ZapiProperties zapiProperties;

public void sendMessage(String phone, String message) {
    ZapiRequest request = new ZapiRequest(phone, message);
    log.info("📤 Enviando mensagem para {} | Conteúdo: {}", phone, message);

    try {
        whatsappClient.sendMessage(
                zapiProperties.getInstanceId(),
                zapiProperties.getToken(),
                request,
                zapiProperties.getAccountToken()
        );

        log.info("✅ Mensagem enviada com sucesso para {}", phone);

    } catch (Exception e) {
        log.error("❌ Erro técnico ao enviar mensagem para Z-API: {}", e.getMessage(), e);
        throw new TechnicalException("Falha técnica ao enviar mensagem para Z-API", e);
    }
  }
}


