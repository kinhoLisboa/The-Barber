package com.theBarber.TheBarber.WSServices;

import com.theBarber.TheBarber.Client.model.Appointment;

public interface ObserverAppointment {

    void onCreated(Appointment appointment);
    void onConfirmed(Appointment appointment);

}
