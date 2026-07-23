package com.theBarber.TheBarber.Client.controller;

import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.service.ClientService;
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
@RequestMapping("/clientes")
@RequiredArgsConstructor
@Log4j2
public class ClientController {

    private final ClientService service;

    @PostMapping
    public ResponseEntity<ClientResponse> create (@Valid @RequestBody ClientRequest request){
        log.info("[Init] ClientController - create ");
        ClientResponse client = service.register(request);
        URI location = URI.create("/clientes/" + client.id());
        log.info("[Finish] ClientController - create ");
        return ResponseEntity.created(location).body(client);
    }
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<ListResponseClient> getList(@RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size){
        log.info("[Init] ClientController - getList ");
        Page<ListResponseClient> response = service.list(page,size);
        log.info("[Finish] ClientController - getList ");
        return  response;
    }
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public ClientDetailed datailed(@PathVariable UUID id){
        log.info("[Init] ClientController - datailed ");
        ClientDetailed client = service.fetch(id);
        log.info("[Finish] ClientController - datailed ");
        return client;

    }
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update (@RequestBody @Valid UpdateClient client ,@PathVariable UUID id ){
        log.info("[Init] ClientController - update ");
        service.alter(client, id);
        log.info("[Finish] ClientController - update ");

    }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        log.info("[Init] ClientController - delete ");
        service.delete(id);
        log.info("[Finish] ClientController - delete ");
    }
}
