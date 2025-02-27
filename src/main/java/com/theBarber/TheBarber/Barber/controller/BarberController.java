package com.theBarber.TheBarber.Barber.controller;



import com.theBarber.TheBarber.Barber.DTO.*;
import com.theBarber.TheBarber.Barber.service.BarberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;
@RestController
@RequestMapping("/barbeiro")
@RequiredArgsConstructor
@Log4j2
public class BarberController {

    private final BarberService service;

    @PostMapping
    public ResponseEntity<BarberResponse> create (@Valid @RequestBody BarberRequest request){
        log.info("[Init] BarberController - create ");
        BarberResponse barber = service.register(request);
        URI location = URI.create("/barbeiro/" + barber.id());
        log.info("[Finish] BarberController - create ");
        return ResponseEntity.created(location).body(barber);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ListResponseBarber> getList(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        log.info("[Init] BarberController - getList ");
        Page<ListResponseBarber> response = service.list(page,size);
        log.info("[Finish] BarberController - getList ");
        return  response;
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public BarberDetailed datailed(@PathVariable UUID id){
        log.info("[Init] BarberController - datailed ");
        BarberDetailed barber = service.fetch(id);
        log.info("[Finish] BarberController - datailed ");
        return barber;

    }
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update (@RequestBody UpdateBarber barber , @PathVariable UUID id ){
        log.info("[Init] BarberController - update ");
        service.alter(barber, id);
        log.info("[Finish] BarberController - update ");

    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        log.info("[Init] BarberController - delete ");
        service.delete(id);
        log.info("[Finish] BarberController - delete ");


    }
}
