package com.theBarber.TheBarber.Barber.service;


import com.theBarber.TheBarber.Barber.DTO.*;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.handle.BarberException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
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
        return new BarberResponse( barber.getId(), barber.getName(),barber.getEmail(),
                barber.getCpf(),barber.getPhone());


    }

    public Page<ListResponseBarber> list(int page, int size) {
        log.info("[Init] BarberService - list ");
        Pageable pageable = PageRequest.of(page, size);
        log.info("[Finish] BarberService - list ");
        return repository.findAll(pageable).map(ListResponseBarber::list);
    }

    public BarberDetailed fetch(UUID id) {
        log.info("[Init] BarberService - fetch ");
        existsBarber(id);
        Barber get = repository.findById(id).get();
        log.info("[Finish] BarberService - fetch ");
        return new BarberDetailed(get.getId(), get.getName(),get.getEmail(), get.getCpf(),
                get.getPhone(),get.getAddress());
    }

    public void alter(UpdateBarber barber, UUID id) {
        log.info("[Init] BarberService - alter ");
        existsBarber(id);
        Barber altered = new Barber(barber);
        altered.setId(id);
        repository.save(altered);
        log.info("[Finish] BarberService - alter ");
    }

    public void delete(UUID id) {
        log.info("[Init] BarberService - delete ");
        existsBarber(id);
        Barber barber= repository.getReferenceById(id);
        repository.save(barber);
        log.info("[Finish] BarberService - delete ");
    }

    public void existsBarber(UUID id) {
        if (!repository.existsById(id)) {
            throw BarberException.build(HttpStatus.BAD_REQUEST, "Barbeiro não encontrado !");
        }
    }
}
