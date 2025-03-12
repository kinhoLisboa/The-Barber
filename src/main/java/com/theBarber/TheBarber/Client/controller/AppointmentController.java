package com.theBarber.TheBarber.Client.controller;

import com.theBarber.TheBarber.Barber.DTO.BarberResponse;
import com.theBarber.TheBarber.Barber.DTO.UpdateBarber;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.service.AppointmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
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
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ListResponseAppointment> getList(@RequestParam(defaultValue = "0") int page,
                                                 @RequestParam(defaultValue = "10") int size) {
        log.info("[Init] AppointmentController - getList ");
        Page<ListResponseAppointment> response = appointmentService.list(page, size);
        log.info("[Finish] AppointmentController - getList ");
        return response;
    }
    @PutMapping("/update/{clientId}")
    @ResponseStatus(HttpStatus.OK)
    public UpdateAppointmentResponse updateAppointment(@PathVariable UUID clientId,
                @RequestBody UpdateAppointmentRequest updateRequest) {
        log.info("[Init] AppointmentController - updateAppointment");
        UpdateAppointmentResponse updated = appointmentService.updateAppointmentByClientId(
                clientId, updateRequest);
        log.info("[Finish] AppointmentController - updateAppointment");
        return updated;
    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointment (@PathVariable UUID id){
        log.info("[Init] AppointmentController - deleteAppointment");
        appointmentService.delete(id);
        log.info("[Finish] AppointmentController - deleteAppointment");

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
