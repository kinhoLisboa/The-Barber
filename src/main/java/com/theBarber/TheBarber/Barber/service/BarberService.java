package com.theBarber.TheBarber.Barber.service;


import com.theBarber.TheBarber.Barber.DTO.*;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class BarberService {

    private final BarberRepository repository;

    public BarberResponse register(@Valid BarberRequest request) {
        log.info("[Init] BarberService - register ");
        Barber barber = repository.save(new Barber(request));
        log.info("[Finish] BarberService - register ");
        return new BarberResponse( barber.getName(),barber.getEmail(),
                barber.getCpf(),barber.getPhone());


    }

    public Page<ListResponseBarber> list(int page, int size) {
        log.info("[Init] BarberService - list ");
        Pageable pageable = PageRequest.of(page, size);
        log.info("[Finish] BarberService - list ");
        return repository.findAll(pageable).map(ListResponseBarber::list);
    }

    public BarberDetailed fetch(UUID id) {
        return null;
    }

    public void alter(UpdateBarber barber, UUID id) {
    }

    public void delete(UUID id) {
    }
}
