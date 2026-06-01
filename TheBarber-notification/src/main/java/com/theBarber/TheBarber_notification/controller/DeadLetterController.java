package com.theBarber.TheBarber_notification.controller;

import com.theBarber.TheBarber_notification.service.DeadLetterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications/dlq")
@RequiredArgsConstructor
@Log4j2
public class DeadLetterController {

    private final DeadLetterService service;

    @PostMapping("/all")
    public ResponseEntity<String> reprocessAllMessages() {
        log.info("♻️ Iniciando reprocessamento manual das mensagens DLQ...");

        service.reprocessAll();

        return ResponseEntity.ok("✅ Reprocessamento concluído com sucesso!");
    }
    @DeleteMapping("/clean")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(){
        service.cleanMessages();
    }
}
