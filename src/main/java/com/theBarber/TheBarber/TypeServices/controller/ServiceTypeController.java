package com.theBarber.TheBarber.TypeServices.controller;

import com.theBarber.TheBarber.TypeServices.DTO.ListResponseTypeService;
import com.theBarber.TheBarber.TypeServices.DTO.UpdateServiceTypes;
import com.theBarber.TheBarber.TypeServices.service.ServiceTypeService;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/tipoServiços")
@RequiredArgsConstructor
@Log4j2
public class ServiceTypeController {

    private final ServiceTypeService service;


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ListResponseTypeService> getList(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        log.info("[Init] ServiceTypeController - getList ");
        Page<ListResponseTypeService> response = service.list(page, size);
        log.info("[Finish] ServiceTypeController - getList ");
        return response;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ServiceTypeResponse create(@RequestBody ServiceTypeRequest newService) {
        log.info("[Init] ServiceTypeController - create ");
        ServiceTypeResponse response = service.register(newService);
        log.info("[Finish] ServiceTypeController - create ");
        return response;
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update (@RequestBody UpdateServiceTypes types , @PathVariable UUID id ){
        log.info("[Init] ServiceTypeController - update ");
        service.alter(types, id);
        log.info("[Finish] ServiceTypeController - update ");

    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        log.info("[Init] ServiceTypeController - delete ");
        service.delete(id);
        log.info("[Finish] ServiceTypeController - delete ");
    }
}

