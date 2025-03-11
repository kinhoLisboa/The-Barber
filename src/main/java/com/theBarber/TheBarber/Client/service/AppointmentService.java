package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Barber.DTO.UpdateBarber;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.AppointmentRepository;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentService {

    private static final Duration ATTENDANCE_DURATION = Duration.ofMinutes(60);

    private final AppointmentRepository appointmentRepository;

    private final BarberRepository barberRepository;

    private  final ClientRepository clientRepository;

    public AppointmentResponse createAppointment(UUID barberId, UUID clientId, LocalDateTime appointmentTime) {
        log.info("[Init] AppointmentService - createAppointment ");
        Barber barber = existsBarber(barberId);
        Client client = existsClient(clientId);
        validateAppointmentTime(barber,appointmentTime);
        Appointment appointment = create(barber,client,appointmentTime);
        log.info("[Finish] AppointmentService - createAppointment ");
        return new AppointmentResponse(appointment.getId(),
                appointment.getAppointmentTime(),appointment.getStatus());
    }
    public Page<ListResponseAppointment> list(int page, int size) {
        log.info("[Init] AppointmentService - listAppointments ");
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments = appointmentRepository.findAllWithBarberAndClient(pageable);
        log.info("[Finish] AppointmentService - listAppointments ");
        return appointments.map(ListResponseAppointment::new);
    }

    public UpdateAppointmentResponse updateAppointmentByClientName(String clientName,
                                                                   UpdateAppointmentRequest updateRequest) {
        log.info("[Init] AppointmentService - updateAppointmentByClientName");
        Appointment appointment = appointmentRepository.findByClientName(clientName)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,
                        "Agendamento não encontrado para o cliente: " + clientName));

        if (updateRequest.newDate() != null) {
            appointment.setAppointmentTime(updateRequest.newDate());
        }
        if (updateRequest.newBarberName()!= null) {
            Barber newBarber = barberRepository.findByName(updateRequest.newBarberName())
                    .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,
                            "Barbeiro não encontrado com esse nome: "));
            appointment.setBarber(newBarber);
        }
        appointmentRepository.save(appointment);
        log.info("[Finish] AppointmentService - updateAppointmentByClientName");
        return new UpdateAppointmentResponse(appointment);
    }

    public AppointmentResponse changeAppointmentStatus(UUID appointmentId, AppointmentStatus newStatus) {
        log.info("[Init] AppointmentService - changeAppointmentStatus ");
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->  BarberException.build(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        appointment.changeStatus(newStatus);
        appointmentRepository.save(appointment);
        log.info("[Finish] AppointmentService - changeAppointmentStatus ");
        return new AppointmentResponse(appointment.getId(), appointment.getAppointmentTime(), appointment.getStatus());
    }

    private Barber existsBarber(UUID barberId) {
        return barberRepository.findById(barberId)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST, "Barbeiro não encontrado"));
    }
    private Client existsClient(UUID clientId) {
        return clientRepository.findById(clientId)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST, "Cliente não encontrado"));
    }
    private Appointment create(Barber barber, Client client, LocalDateTime appointmentTime) {
        Appointment appointment = new Appointment();
        appointment.setBarber(barber);
        appointment.setClient(client);
        appointment.setAppointmentTime(appointmentTime);
        return appointmentRepository.save(appointment);
    }
    private void validateAppointmentTime(Barber barber, LocalDateTime appointmentTime) {
        // 1️ Validação do agendamento no passado
        validateAppointmentInPast(appointmentTime);
        // 2️ Validação do horário de expediente
        validateAppointmentWithinBusinessHours(appointmentTime);
        // 3️ Verificar se o horário já está ocupado
        validateAppointmentTimeAvailability(barber, appointmentTime);
        // 4️ Verificar se há sobreposição de horários
        validateAppointmentOverlap(barber, appointmentTime);
        // 5️ Verificar se o novo agendamento está pelo menos 60 minutos após o último agendamento
        validateTimeBetweenAppointments(barber, appointmentTime);
    }
    private void validateAppointmentInPast(LocalDateTime appointmentTime) {
        if (appointmentTime.isBefore(LocalDateTime.now())) {
            throw BarberException.build(HttpStatus.BAD_REQUEST,
                    "O horário do agendamento não pode estar no passado.");
        }
    }
    private void validateAppointmentWithinBusinessHours(LocalDateTime appointmentTime) {
        LocalTime openingTime = LocalTime.of(9, 0);
        LocalTime closingTime = LocalTime.of(19, 0);
        LocalTime requestedTime = appointmentTime.toLocalTime();

        if (requestedTime.isBefore(openingTime) || requestedTime.isAfter(closingTime)) {
            throw BarberException.build(HttpStatus.BAD_REQUEST,
                    "O agendamento deve ser feito dentro do horário de expediente (09:00 - 19:00).");
        }
    }
    private void validateAppointmentTimeAvailability(Barber barber, LocalDateTime appointmentTime) {
        boolean isTimeTaken = appointmentRepository.existsByBarberAndAppointmentTime(barber, appointmentTime);
        if (isTimeTaken) {
            throw BarberException.build(HttpStatus.CONFLICT,
                    "Este horário já está ocupado. Escolha outro horário.");
        }
    }
    private void validateAppointmentOverlap(Barber barber, LocalDateTime appointmentTime) {
        LocalDateTime endOfRequestedTime = appointmentTime.plus(ATTENDANCE_DURATION);

        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(barber.getId(), appointmentTime, endOfRequestedTime);

        if (!overlappingAppointments.isEmpty()) {
            throw BarberException.build(HttpStatus.CONFLICT,
                    "Este horário entra em conflito com outro agendamento. Escolha outro horário.");
        }
    }
    private void validateTimeBetweenAppointments(Barber barber, LocalDateTime appointmentTime) {
        List<Appointment> appointments = appointmentRepository.findByBarberId(barber.getId());

        if (!appointments.isEmpty()) {
            Appointment lastAppointment = appointments.stream()
                    .max(Comparator.comparing(Appointment::getAppointmentTime))
                    .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,
                            "Erro ao encontrar o último agendamento"));

            if (lastAppointment.getStatus() == AppointmentStatus.FINALIZED) {
                return;
            }
            LocalDateTime lastStartTime = lastAppointment.getAppointmentTime();
            if (appointmentTime.isBefore(lastStartTime.plusMinutes(60))) {
                throw BarberException.build(HttpStatus.CONFLICT,
                        "O novo agendamento deve ser feito pelo menos 60 minutos após o início do agendamento anterior.");
            }
        }
    }

}
