package com.theBarber.TheBarber_notification.consumer;

import com.theBarber.TheBarber_notification.dto.AppointmentEvent;
import com.theBarber.TheBarber_notification.model.DeadLetterMessage;
import com.theBarber.TheBarber_notification.repository.DeadLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Service
@RequiredArgsConstructor
@Log4j2
@Component
public class DeadLetterListener {

    private final DeadLetterRepository repository;


    @RabbitListener(queues = "notification.queue.dlq")
    public void receiveDeadLetter(AppointmentEvent event) {
        log.warn("📥 Mensagem recebida da DLQ: {}", event);

        DeadLetterMessage entity = DeadLetterMessage.builder()
                .clientName(event.clientName())
                .clientPhone(event.clientPhone())
                .appointmentTime(event.appointmentTime())
                .type(event.type())
                .errorDescription("Erro no processamento original")
                .receivedAt(LocalDateTime.now())
                .status(DeadLetterMessage.Status.PENDENTE)
                .build();

           repository.save(entity);

        log.info("💾 Mensagem da DLQ persistida no banco com sucesso (id={})", entity.getId());
    }
}
