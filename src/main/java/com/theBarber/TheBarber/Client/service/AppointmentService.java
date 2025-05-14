package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Barber.DTO.UpdateBarber;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.model.StatusBarber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.AppointmentRepository;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import com.theBarber.TheBarber.TypeServices.repository.ServiceTypeRepository;
import com.theBarber.TheBarber.WSServices.WhatsAppService;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BarberRepository barberRepository;
    private final ClientRepository clientRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final NotificationService notificationService;
    private final AppointmentValidator appointmentValidator;

    public AppointmentResponse createAppointment(UUID barberId, UUID clientId,
                                                 LocalDateTime appointmentTime, List<UUID> servicesId) {
        log.info("[Init] AppointmentService - createAppointment ");
        Barber barber = existsBarber(barberId);
        Client client = existsClient(clientId);

        appointmentValidator.validateBarberStatus(barber);
        appointmentValidator.validateAppointmentTime(barber, appointmentTime);

        Appointment appointment = create(barber, client, appointmentTime, servicesId);

        log.info("[Finish] AppointmentService - createAppointment ");
        return new AppointmentResponse(appointment.getId(),
                appointment.getAppointmentTime(), appointment.getStatus(), appointment.getServices()
                .stream().map(s -> new ServiceTypeResponse(s.getId(), s.getName(), s.getPrice()))
                .toList());
    }

    public Page<ListResponseAppointment> list(int page, int size) {
        log.info("[Init] AppointmentService - listAppointments ");
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments = appointmentRepository.findAllWithBarberAndClient(pageable);
        if (appointments.isEmpty()) {
            throw BarberException.build(HttpStatus.NOT_FOUND, "Nenhum agendamento encontrado.");
        }
        log.info("[Finish] AppointmentService - listAppointments ");
        return appointments.map(ListResponseAppointment::new);
    }

    public UpdateAppointmentResponse updateAppointmentByClientId(UUID clientId,
                                                                 UpdateAppointmentRequest updateRequest) {
        log.info("[Init] AppointmentService - updateAppointmentByClientId");
        existsClient(clientId);
        Appointment appointment = appointmentRepository.findByClientId(clientId).get();

        if (updateRequest.newDate() != null) {
            appointment.setAppointmentTime(updateRequest.newDate());
        }
        if (updateRequest.newBarberId() != null) {
            Barber newBarber = barberRepository.findById(updateRequest.newBarberId())
                    .orElseThrow(() -> BarberException.build(HttpStatus.NOT_FOUND, "Barbeiro não encontrado!"));
            appointment.setBarber(newBarber);
        }
        appointmentRepository.save(appointment);
        log.info("[Finish] AppointmentService - updateAppointmentByClientId");
        return new UpdateAppointmentResponse(appointment);
    }

    public void delete(UUID id) {
        log.info("[Init] AppointmentService - delete");
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST, "Agendamento não encontrado!"));
        appointment.getServices().clear();
        appointmentRepository.delete(appointment);
        log.info("[Finish] AppointmentService - delete");
    }

    public AppointmentResponse changeAppointmentStatus(UUID appointmentId, AppointmentStatus newStatus) {
        log.info("[Init] AppointmentService - changeAppointmentStatus ");
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> BarberException.build(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

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

    private Appointment create(Barber barber, Client client, LocalDateTime appointmentTime, List<UUID> servicesId) {
        List<ServiceTypes> services = serviceTypeRepository.findAllById(servicesId);
        if (services.isEmpty()) {
            throw BarberException.build(HttpStatus.NOT_FOUND, "Serviços não encontrados!");
        }

        Appointment appointment = new Appointment();
        appointment.setBarber(barber);
        appointment.setClient(client);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setServices(services);
        appointment = appointmentRepository.save(appointment);

        notificationService.sendAppointmentConfirmation(client, appointment.getAppointmentTime());
        return appointment;
    }


}
