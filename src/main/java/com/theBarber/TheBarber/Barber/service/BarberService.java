package com.theBarber.TheBarber.Barber.service;
import com.theBarber.TheBarber.Barber.DTO.*;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.model.Role;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.handle.BarberException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class BarberService {

    private final BarberRepository repository;
    private final PasswordEncoder passwordEncoder;

    public BarberResponse register(@Valid BarberRequest request) {
        log.info("[Init] BarberService - register ");
        existsAdmin(request);
        existEmail(request);
        validateAdminCreationPermission();
        String encryptedPassword = passwordEncoder.encode(request.password());
        Barber barber = repository.save(new Barber(request, encryptedPassword));
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
        repository.delete(barber);
        log.info("[Finish] BarberService - delete ");
    }

    public void existsBarber(UUID id) {
        if (!repository.existsById(id)) {
            throw BarberException.build(HttpStatus.BAD_REQUEST, "Barbeiro não encontrado !");
        }
    }
    public void existsAdmin(BarberRequest request){
        if (request.role() == Role.BARBEIRO && !repository.existsByRole(Role.ADMIN)) {
            throw BarberException.build(HttpStatus.BAD_REQUEST,"Você precisa cadastrar um administrador antes de criar barbeiros.");
        }
        if (request.role() == Role.ADMIN) {
            boolean exists = repository.existsByRole(Role.ADMIN);
            if (exists) {
                throw BarberException.build(HttpStatus.BAD_REQUEST,"Já existe um administrador cadastrado.");
            }
        }
    }
    public void existEmail(BarberRequest request){
        if (repository.existsByEmail(request.email())) {
            throw BarberException.build(HttpStatus.BAD_REQUEST,
                    "Já existe um barbeiro com este e-mail.");
        }
    }
    public void validateAdminCreationPermission() {
        boolean adminExists = repository.existsByRole(Role.ADMIN);

        if (adminExists) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null || !authentication.isAuthenticated()) {
                throw BarberException.build(HttpStatus.BAD_REQUEST,"Você precisa estar autenticado como ADMIN para criar um barbeiro.");
            }
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!isAdmin) {
                throw BarberException.build(HttpStatus.BAD_REQUEST,"Apenas administradores podem cadastrar barbeiros.");
            }
        }
    }


}
