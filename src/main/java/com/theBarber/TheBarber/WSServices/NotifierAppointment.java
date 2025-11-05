package com.theBarber.TheBarber.WSServices;

import com.theBarber.TheBarber.Client.model.Appointment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
@Component
@RequiredArgsConstructor
public class NotifierAppointment {

    private final List<ObserverAppointment> observers = new ArrayList<>();

    public void register(ObserverAppointment observer) {
        observers.add(observer);
    }

    public void notifyCreated(Appointment appointment) {
        for (ObserverAppointment o : observers) {
            o.onCreated(appointment);
        }
    }

    public void notifyConfirmed(Appointment appointment) {
        for (ObserverAppointment o : observers) {
            o.onConfirmed(appointment);
        }
    }
}
