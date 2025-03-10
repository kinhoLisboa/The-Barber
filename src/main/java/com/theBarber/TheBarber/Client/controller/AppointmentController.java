package com.theBarber.TheBarber.Client.controller;

import com.theBarber.TheBarber.Client.DTO.AppointmentRequest;
import com.theBarber.TheBarber.Client.DTO.AppointmentResponse;
import com.theBarber.TheBarber.Client.DTO.AppointmentStatusRequest;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/agendamentos")
@Log4j2
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse create (@RequestBody @Valid AppointmentRequest request){
        log.info("[Init] AppointmentController - create ");
        AppointmentResponse response = appointmentService.createAppointment(request.barberId(),
                request.clientId(), request.appointmentTime());
        log.info("[Finish] AppointmentController - create ");
        return response;

    }
    @PatchMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public AppointmentResponse changeStatus(@PathVariable UUID id, @RequestBody AppointmentStatusRequest status) {
        log.info("[Init] AppointmentController - changeStatus ");
        AppointmentResponse response = appointmentService.changeAppointmentStatus(id, status.newStatus());
        log.info("[Finish] AppointmentController - changeStatus ");
        return response;
    }
}
