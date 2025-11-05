package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.repository.AppointmentRepository;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class AppointmentValidator {

    private static final Duration ATTENDANCE_DURATION = Duration.ofMinutes(60);
    private final AppointmentRepository appointmentRepository;

    public void validateClientPermission(Appointment appointment, String userEmailLogado, AppointmentStatus newStatus) {
        boolean usuarioValido = userEmailLogado == null || appointment.getClient().getEmail().equals(userEmailLogado);
        boolean statusPodeSerConfirmado = appointment.getStatus().equals(newStatus);

        if (!usuarioValido) {
            throw BarberException.build(HttpStatus.FORBIDDEN, "Apenas o cliente dono do agendamento pode alterá-lo.");
        }

        if (statusPodeSerConfirmado) {
            throw BarberException.build(HttpStatus.FORBIDDEN, "Agendamento não pode ser confirmado neste estado.");
        }
    }

    public void validateAppointmentTime(Barber barber, LocalDateTime appointmentTime) {
        validateAppointmentInPast(appointmentTime);
        validateAppointmentWithinBusinessHours(appointmentTime);
        validateAppointmentTimeAvailability(barber, appointmentTime);
        validateAppointmentOverlap(barber, appointmentTime);
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

        List<Appointment> overlappingAppointments = appointmentRepository.findOverlappingAppointments(
                barber.getId(), appointmentTime, endOfRequestedTime);

        if (!overlappingAppointments.isEmpty()) {
            throw BarberException.build(HttpStatus.CONFLICT,
                    "Este horário entra em conflito com outro agendamento. Escolha outro horário.");
        }
    }

    private void validateTimeBetweenAppointments(Barber barber, LocalDateTime appointmentTime) {
        List<Appointment> appointments = appointmentRepository.findByBarberId(barber.getId());

        if (!appointments.isEmpty()) {
            LocalDateTime newStart = appointmentTime;
            LocalDateTime newEnd = appointmentTime.plusMinutes(60);

            for (Appointment existing : appointments) {

                if (existing.getAppointmentTime().toLocalDate().equals(newStart.toLocalDate())) {

                    LocalDateTime existingStart = existing.getAppointmentTime();
                    LocalDateTime existingEnd = existingStart.plusMinutes(60);

                    boolean overlap = newStart.isBefore(existingEnd) && newEnd.isAfter(existingStart);

                    if (overlap) {
                        throw BarberException.build(HttpStatus.CONFLICT,
                                "Conflito de horário: cada agendamento deve ter pelo menos 60 minutos de duração.");
                    }
                }
            }
        }
    }

}
