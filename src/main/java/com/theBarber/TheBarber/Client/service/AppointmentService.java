package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.Barber.service.BarberService;
import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.AppointmentRepository;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import com.theBarber.TheBarber.TypeServices.repository.ServiceTypeRepository;
import com.theBarber.TheBarber.WSServices.NotifierAppointment;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final BarberRepository barberRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final AppointmentValidator appointmentValidator;
    private final ClientService clientService;
    private final BarberService barberService;
    private final NotifierAppointment notifierAppointment;

    public AppointmentResponse createAppointment(UUID barberId, UUID clientId,
                                                 LocalDateTime appointmentTime, List<UUID> servicesId) {
        log.info("[Init] AppointmentService - createAppointment ");
        Barber barber = barberService.existsBarber(barberId);
        Client client = clientService.existsClient(clientId);
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
        clientService.existsClient(clientId);
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
                .orElseThrow(() -> BarberException.build(HttpStatus.NOT_FOUND, "Agendamento não encontrado!"));
        appointment.getServices().clear();
        appointmentRepository.delete(appointment);
        log.info("[Finish] AppointmentService - delete");
    }

    public AppointmentResponse changeAppointmentStatus(UUID appointmentId, AppointmentStatus newStatus,
                                                       String userEmailLogado) {
        log.info("[Init] AppointmentService - changeAppointmentStatus");
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> BarberException.build(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        if (barberService.isBarber(userEmailLogado)) {
            return processBarberStatusChange(appointment, newStatus);
        }
        log.info("[Finish] AppointmentService - changeAppointmentStatus");
        return processClientStatusChange(appointment, newStatus, userEmailLogado);
    }

    private AppointmentResponse processBarberStatusChange(Appointment appointment, AppointmentStatus newStatus) {
        log.info("[Init] AppointmentService - processBarberStatusChange");
        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        notifierAppointment.notifyConfirmed(appointment);
        log.info("[Finish] AppointmentService - processBarberStatusChange");
        return new AppointmentResponse(appointment.getId(), appointment.getAppointmentTime(),
                appointment.getStatus());
    }

    private AppointmentResponse processClientStatusChange(Appointment appointment, AppointmentStatus newStatus,
                                                          String userEmailLogado) {
        log.info("[Init] AppointmentService - processClientStatusChange");
        appointmentValidator.validateClientPermission(appointment, userEmailLogado, newStatus);
        appointment.setStatus(newStatus);
        appointmentRepository.save(appointment);
        notifierAppointment.notifyConfirmed(appointment);
        log.info("[Finish] AppointmentService - processClientStatusChange");
        return new AppointmentResponse(appointment.getId(), appointment.getAppointmentTime(),
                appointment.getStatus());
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
        notifierAppointment.notifyCreated(appointment);

        return appointment;
    }


}
