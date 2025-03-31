package com.theBarber.TheBarber.TypeServices.service;

import com.theBarber.TheBarber.TypeServices.DTO.ListResponseTypeService;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeRequest;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;
import com.theBarber.TheBarber.TypeServices.DTO.UpdateServiceTypes;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import com.theBarber.TheBarber.TypeServices.repository.ServiceTypeRepository;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Log4j2
@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;

    public ServiceTypeResponse register (ServiceTypeRequest newService) {
        log.info("[Init] ServiceTypeRepository - register ");
        ServiceTypes types = repository.save(new ServiceTypes(newService));
        log.info("[Finish] ServiceTypeRepository - register ");
        return new ServiceTypeResponse(types.getId(),types.getName(),types.getPrice());
    }

    public Page<ListResponseTypeService> list(int page, int size) {
        log.info("[Init] ServiceTypeService - list ");
        Pageable pageable = PageRequest.of(page, size);
        log.info("[Finish] ServiceTypeService - list ");
        return repository.findAll(pageable).map(ListResponseTypeService::list);
    }
    public void alter(UpdateServiceTypes types, UUID id) {
        log.info("[Init] ServiceTypeRepository - alter ");
        exists(id);
        ServiceTypes altered = new ServiceTypes(types);
        altered.setId(id);
        repository.save(altered);
        log.info("[Finish] ServiceTypeRepository - alter ");
    }

    public void delete(UUID id) {
        log.info("[Init] ServiceTypeRepository - delete ");
        exists(id);
        ServiceTypes type = repository.getReferenceById(id);
        repository.delete(type);
        log.info("[Finish] ServiceTypeRepository - delete ");
    }

    public void exists(UUID id) {
        if (!repository.existsById(id)) {
            throw BarberException.build(HttpStatus.BAD_REQUEST, "Serviço não encontrado !");
        }
    }
}
