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

    private static final Duration ATTENDANCE_DURATION = Duration.ofMinutes(60);

    private final AppointmentRepository appointmentRepository;

    private final BarberRepository barberRepository;

    private  final ClientRepository clientRepository;

    private final ServiceTypeRepository serviceTypeRepository;

    private final WhatsAppService whatsappService;

    public AppointmentResponse createAppointment(UUID barberId, UUID clientId,
                                                 LocalDateTime appointmentTime, List<UUID> servicesId) {
        log.info("[Init] AppointmentService - createAppointment ");
        Barber barber = existsBarber(barberId);
        Client client = existsClient(clientId);
        validateAppointmentTime(barber,appointmentTime);
        Appointment appointment = create(barber,client,appointmentTime,servicesId);

        log.info("[Finish] AppointmentService - createAppointment ");
        return new AppointmentResponse(appointment.getId(),
                appointment.getAppointmentTime(),appointment.getStatus(),appointment.getServices()
                .stream().map(s -> new ServiceTypeResponse(s.getId(),s.getName(), s.getPrice()))
                .toList());
    }

    public Page<ListResponseAppointment> list(int page, int size) {
        log.info("[Init] AppointmentService - listAppointments ");
        Pageable pageable = PageRequest.of(page, size);
        Page<Appointment> appointments = appointmentRepository.findAllWithBarberAndClient(pageable);
        if (appointments.isEmpty()) {
            throw BarberException.build(HttpStatus.NOT_FOUND,
                    "Nenhum agendamento encontrado.");
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
                    .orElseThrow(() -> BarberException.build(HttpStatus.NOT_FOUND,
                            "Barbeiro não encontrado!"));
            appointment.setBarber(newBarber);
        }
        appointmentRepository.save(appointment);
        log.info("[Finish] AppointmentService - updateAppointmentByClientId");
        return new UpdateAppointmentResponse(appointment);
    }

    public void delete(UUID id) {
        log.info("[Init] AppointmentService - delete");
        Appointment  appointment = appointmentRepository.findById(id)
                .orElseThrow(()->BarberException.build(HttpStatus.BAD_REQUEST,
                        "Agendamento não encontrado!"));
        appointment.getServices().clear();
        appointmentRepository.delete(appointment);
        log.info("[Finish] AppointmentService - delete");
    }

    public AppointmentResponse changeAppointmentStatus(UUID appointmentId, AppointmentStatus newStatus) {
        log.info("[Init] AppointmentService - changeAppointmentStatus ");
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() ->  BarberException.build(HttpStatus.NOT_FOUND, "Agendamento não encontrado"));

        appointment.changeStatus(newStatus);
        appointmentRepository.save(appointment);
        log.info("[Finish] AppointmentService - changeAppointmentStatus ");
        return new AppointmentResponse(appointment.getId(), appointment.getAppointmentTime(),
                appointment.getStatus());
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
        if(services.isEmpty()){
            throw BarberException.build(HttpStatus.NOT_FOUND,"Serviços não encotrados!");
        }
        Appointment appointment = new Appointment();
        appointment.setBarber(barber);
        appointment.setClient(client);
        appointment.setAppointmentTime(appointmentTime);
        appointment.setServices(services);
        appointment = appointmentRepository.save(appointment);

        sendConfirmationMessage(client, appointment.getAppointmentTime());
        return appointment;

    }
    private void sendConfirmationMessage(Client client, LocalDateTime appointmentTime) {
        String formattedPhone = formatPhone(client.getPhone());
        String message = "✅ Olá, " + client.getName() + "! Seu agendamento foi confirmado para "
                + appointmentTime.toLocalDate() + " às " + appointmentTime.toLocalTime() + ". Nos vemos em breve! ✂️💈";

        try {
            whatsappService.sendWhatsAppMessage(formattedPhone, message);
            log.info("Mensagem enviada para: " + formattedPhone);
        } catch (Exception e) {
            log.error("Erro ao enviar mensagem para o cliente: " + formattedPhone, e);
        }
    }
    private String formatPhone(String phone) {
        String formattedPhone = phone.replaceAll("[^0-9]", ""); // Remove caracteres não numéricos
        if (!formattedPhone.startsWith("55")) {
            formattedPhone = "55" + formattedPhone; // Adiciona DDI do Brasil se não estiver presente
        }
        return formattedPhone;
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
