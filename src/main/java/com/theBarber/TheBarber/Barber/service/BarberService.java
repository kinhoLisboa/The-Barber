package com.theBarber.TheBarber.Barber.service;


import com.theBarber.TheBarber.Barber.DTO.BarberRequest;
import com.theBarber.TheBarber.Barber.DTO.BarberResponse;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class BarberService {

    private final BarberRepository repository;

    public BarberResponse register(@Valid BarberRequest request) {
        log.info("[Init] BarberService - register ");

        log.info("[Finish] BarberService - register ");


    }
}
