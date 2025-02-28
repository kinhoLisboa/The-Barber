package com.theBarber.TheBarber.Client.repository;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;
@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {
    boolean existsByBarberAndAppointmentTime(Barber barber, LocalDateTime appointmentTime);


}
