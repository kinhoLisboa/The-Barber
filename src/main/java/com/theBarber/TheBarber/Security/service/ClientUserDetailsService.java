package com.theBarber.TheBarber.Security.service;

import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class ClientUserDetailsService implements UserDetailsService {

    private final ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Cliente não encontrado com o email: " + email));

        return new User(client.getEmail(), client.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_CLIENT")));
    }
}
