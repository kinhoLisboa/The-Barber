package com.theBarber.TheBarber_notification.observer;

import com.theBarber.TheBarber_notification.dto.AppointmentEvent;

public interface ObserverNotification {

    void notify (AppointmentEvent event);

}
