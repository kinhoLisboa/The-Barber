package com.theBarber.TheBarber.TypeServices.controller;


import com.theBarber.TheBarber.TypeServices.service.ServiceTypeService;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController("/tipoServicos")
@RequiredArgsConstructor
@Log4j2
public class ServiceTypeController {

    private final ServiceTypeService service;


    @GetMapping
    public ResponseEntity<List<Service>> listAll() {
        return ResponseEntity.ok(serviceService.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Service> findById(@PathVariable UUID id) {
        return ResponseEntity.ok(serviceService.findById(id));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceTypeResponse create(@RequestBody ServiceTypeRequest newService) {
        log.info("[Init] ServiceTypeController - create ");
        ServiceTypeResponse response = service.registes(newService);
        log.info("[Finish] ServiceTypeController - create ");
        return response;
    }

    @PutMapping("/{id}")
    public ResponseEntity<Service> updateService(@PathVariable UUID id, @RequestBody Service updatedService) {
        return ResponseEntity.ok(serviceService.updateService(id, updatedService));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteService(@PathVariable UUID id) {
        serviceService.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
