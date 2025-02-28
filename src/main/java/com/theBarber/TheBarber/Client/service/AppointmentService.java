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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentService {

    private static final Duration ATTENDANCE_DURATION = Duration.ofHours(1);

    private final AppointmentRepository appointmentRepository;

    private final BarberRepository barberRepository;

    private  final ClientRepository clientRepository;


    public AppointmentResponse createAppointment(UUID barberId, UUID clientId, LocalDateTime appointmentTime) {
        log.info("[Init] AppointmentController - create ");
        Barber barber = barberRepository.findById(barberId)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,"Barbeiro não encontrado"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,"Cliente não encontrado"));

        validateAppointmentTime(barber,appointmentTime);

        Appointment appointment = new Appointment();
        appointment.setBarber(barber);
        appointment.setClient(client);
        appointment.setAppointmentTime(appointmentTime);
        appointmentRepository.save(appointment);
        log.info("[Finish] AppointmentController - create ");
        return new AppointmentResponse(appointment.getId(),
                appointment.getAppointmentTime(),appointment.getStatus());
    }

    private void validateAppointmentTime(Barber barber, LocalDateTime appointmentTime) {

        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw BarberException.build(HttpStatus.BAD_REQUEST,
                     "O horário do agendamento não pode estar no passado.");
        }
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(19, 0);
        LocalTime requestedTime = appointmentTime.toLocalTime();

        if (requestedTime.isBefore(openingTime) || requestedTime.isAfter(closingTime)) {
            throw BarberException.build(HttpStatus.BAD_REQUEST, "O agendamento deve ser feito dentro do horário de expediente (09:00 - 19:00).");
        }
        boolean isTimeTaken = appointmentRepository.existsByBarberAndAppointmentTime(barber, appointmentTime);
        if (isTimeTaken) {
            throw BarberException.build(HttpStatus.CONFLICT, "Este horário já está ocupado. Escolha outro horário.");
        }
        LocalDateTime endOfRequestedTime = appointmentTime.plus(ATTENDANCE_DURATION);

        boolean isConflict = appointmentRepository.existsByBarberAndAppointmentTimeBetween(barber, appointmentTime, endOfRequestedTime);
        if (isConflict) {
            throw BarberException.build(HttpStatus.CONFLICT, "Este horário entra em conflito com outro agendamento. Escolha outro horário.");
        }
    }

}
