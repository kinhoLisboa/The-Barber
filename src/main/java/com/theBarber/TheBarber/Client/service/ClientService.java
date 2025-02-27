package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import com.theBarber.TheBarber.handle.BarberException;
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
public class ClientService {

    private final ClientRepository repository;

    public ClientResponse register(CLientRequest request) {
        log.info("[Init] ClientService - register ");
        if(repository.existsByEmail(request.email())) {
            throw BarberException.build(HttpStatus.BAD_REQUEST, " Esse email ja existe!");
        }
        Client client = repository.save(new Client(request));
        log.info("[Finish] ClientService - register ");
        return new ClientResponse(client.getId(), client.getName(), client.getEmail(), client.getPhone());
    }

    public Page<ListResponseClient> list(int page, int size) {
        log.info("[Init] ClientService - list ");
        Pageable pageable = PageRequest.of(page, size);
        log.info("[Finish] ClientService - list ");
        return repository.findAll(pageable).map(ListResponseClient::list);
    }


    public ClientDetailed fetch(UUID id) {
        log.info("[Init] ClientService - fetch ");
        exists(id);
        Client findBy = repository.findById(id).get();
        log.info("[Finish] ClientService - fetch ");
        return new ClientDetailed(
                 findBy.getName(),findBy.getEmail(),findBy.getPhone());
    }

    public void alter(UpdateClient client, UUID id) {
        log.info("[Init] ClientService - alter ");
        exists(id);
        Client altered = new Client(client);
        altered.setId(id);
        repository.save(altered);
        log.info("[Finish] ClientService - alter ");

    }

    public void delete(UUID id) {
        log.info("[Init] ClientService - delete ");
        exists(id);
        Client client = repository.getReferenceById(id);
        repository.delete(client);
        log.info("[Finish] ClientService - delete ");
    }
    public void exists(UUID id){
        if(!repository.existsById(id)){
            throw BarberException.build(HttpStatus.BAD_REQUEST,"Cliente não encontrado !");
        }
    }
}