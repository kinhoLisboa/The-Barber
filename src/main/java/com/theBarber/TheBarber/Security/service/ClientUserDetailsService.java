package com.theBarber.TheBarber.Security.service;

import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@AllArgsConstructor
public class ClientUserDetailsService implements UserDetailsService {

    private ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws BarberException {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> BarberException.build(HttpStatus.BAD_REQUEST,
                        "Cliente não encontrado com o email: " + email));

        return new User(client.getEmail(), client.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));
    }
}
