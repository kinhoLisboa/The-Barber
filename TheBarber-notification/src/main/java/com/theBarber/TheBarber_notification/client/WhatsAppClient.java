package com.theBarber.TheBarber_notification.client;

import com.theBarber.TheBarber_notification.dto.ZapiRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "whatsappClient", url = "${zapi.base-url}")
public interface WhatsAppClient {

        @PostMapping("/instances/{instanceId}/token/{token}/send-text")
        void sendMessage(
                @PathVariable("instanceId") String instanceId,
                @PathVariable("token") String token,
                @RequestBody ZapiRequest request,
                @RequestHeader("Client-Token") String accountToken
        );

}
