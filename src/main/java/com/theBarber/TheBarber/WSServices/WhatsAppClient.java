package com.theBarber.TheBarber.WSServices;

import feign.Headers;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.Map;

@FeignClient(name = "whatsappClient", url =
        "https://api.z-api.io/instances/3DE6763FBCFEB07569E14AF752FBD4C9/token/2381E4A2AE4DA5D7791337D1")
public interface WhatsAppClient {

        @PostMapping("/token/2381E4A2AE4DA5D7791337D1/send-text")
        @Headers("Content-Type: application/json")
        ResponseEntity<String> sendWhatsAppMessage(@RequestHeader("Client-Token") String token,
                                                   @RequestBody Map<String, String> requestBody);

}
