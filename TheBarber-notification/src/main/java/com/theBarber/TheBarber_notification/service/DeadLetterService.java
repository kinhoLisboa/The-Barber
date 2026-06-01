package com.theBarber.TheBarber_notification.service;

import com.theBarber.TheBarber_notification.config.RabbitConfig;
import com.theBarber.TheBarber_notification.dto.AppointmentEvent;
import com.theBarber.TheBarber_notification.model.DeadLetterMessage;
import com.theBarber.TheBarber_notification.repository.DeadLetterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
@Component
public class DeadLetterService  {

    private final DeadLetterRepository repository;
    private final RabbitTemplate rabbitTemplate;

    public void reprocessAll() {
        log.info("♻️ Iniciando reprocessamento de mensagens DLQ...");

        var pendentes = repository.findByStatus(DeadLetterMessage.Status.PENDENTE);

        if (pendentes.isEmpty()) {
            log.info("✅ Nenhuma mensagem pendente para reprocessar.");
            return;
        }
        log.info("📋 Total de mensagens pendentes: {}", pendentes.size());

        pendentes.forEach(this::safeReprocess);
        log.info("🏁 Reprocessamento concluído!");
    }

    private void safeReprocess(DeadLetterMessage msg) {
        try {
            reprocessMessage(msg);
        } catch (Exception e) {
            log.error("❌ Falha ao reenfileirar ID={} | Erro: {}", msg.getId(), e.getMessage(), e);
        }
    }

    private void reprocessMessage(DeadLetterMessage msg) {
        log.info(
                "➡️ Reenfileirando ID={} | Cliente={} | Telefone={}",
                msg.getId(), msg.getClientName(), msg.getClientPhone()
        );
        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY,
                toEvent(msg)
        );
        msg.setStatus(DeadLetterMessage.Status.REPROCESSADO);
        repository.save(msg);
        log.info("✅ Mensagem ID={} reenviada com sucesso!", msg.getId());
    }

    private AppointmentEvent toEvent(DeadLetterMessage msg) {
        return new AppointmentEvent(
                msg.getEventId(),
                msg.getType(),
                msg.getAppointmentId(),
                msg.getClientName(),
                msg.getClientPhone(),
                msg.getAppointmentTime()
        );
    }

    public void cleanMessages() {
        var messages = repository.findByStatus(DeadLetterMessage.Status.REPROCESSADO);
        if(!messages.isEmpty()){
            repository.deleteAll();
        }else {
            throw new RuntimeException("Não existem dados reprocessados!");
        }
    }
    @Scheduled(fixedRate = 30000)
    @SchedulerLock(
            name = "reprocessScheduleLock",
            lockAtLeastFor = "10s",
            lockAtMostFor = "1m"
    )
    public void reprocessSchedule() {

        var messages = repository.findByStatus(
                DeadLetterMessage.Status.PENDENTE
        );
        if (messages.isEmpty()) {
            return;
        }
        log.info("♻️ Iniciando reprocessamento automático de mensagens DLQ...");

        reprocessAll();
        log.info("✔️ Reprocessamento automático concluído.");
    }

}
