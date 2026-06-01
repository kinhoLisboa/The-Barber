package com.theBarber.TheBarber_notification.observer;

import com.theBarber.TheBarber_notification.dto.AppointmentEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationDispatcher {

    private final List<ObserverNotification> observers;

    public void dispatch(AppointmentEvent event) {

        for (ObserverNotification observer : observers) {
            observer.notify(event);
        }
    }
}
