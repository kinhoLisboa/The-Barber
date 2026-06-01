package com.theBarber.TheBarber_notification.consumer;

import com.theBarber.TheBarber_notification.dto.AppointmentEvent;
import com.theBarber.TheBarber_notification.observer.NotificationDispatcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Log4j2
public class NotificationListener {

    private final NotificationDispatcher dispatch;

    @RabbitListener(queues = "notification.queue")
    public void receiveNotification(AppointmentEvent event) {

        log.info("🎧 Mensagem recebida para {}", event.clientPhone());

        dispatch.dispatch(event);



    }

}

