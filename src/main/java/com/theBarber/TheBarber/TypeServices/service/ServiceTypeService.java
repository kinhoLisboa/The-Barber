package com.theBarber.TheBarber.TypeServices.service;

import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeRequest;
import com.theBarber.TheBarber.TypeServices.DTO.ServiceTypeResponse;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import com.theBarber.TheBarber.TypeServices.repository.ServiceTypeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ServiceTypeService {

    private final ServiceTypeRepository repository;

    public ServiceTypeResponse registes(ServiceTypeRequest newService) {
        log.info("[Init] ClientController - create ");
        ServiceTypes types = repository.save(new ServiceTypes(newService));
        log.info("[Finish] ClientController - create ");
        return new ServiceTypeResponse(types.getName(),types.getPrice());
    }
}
