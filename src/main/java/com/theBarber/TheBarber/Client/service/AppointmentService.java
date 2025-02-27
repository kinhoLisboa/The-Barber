package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.Client.DTO.AppointmentResponse;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.AppointmentRepository;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;

    private final BarberRepository barberRepository;

    private  final ClientRepository clientRepository;



    public AppointmentResponse createAppointment(UUID barberId, UUID clientId, LocalDateTime appointmentTime) {
        log.info("[Init] AppointmentController - create ");
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,"Barbeiro não encontrado"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,"Barbeiro não encontrado"));


        Appointment appointment = new Appointment();
        appointment.setBarber(barber);
        appointment.setClient(client);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setStatus(Appointment.AppointmentStatus.PENDING);

        return appointmentRepository.save(appointment);
    }
}
