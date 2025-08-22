package com.theBarber.TheBarber.Security.service;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.handle.BarberException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class BarberUserDetailsService implements UserDetailsService {

    private final BarberRepository barberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Barber barber = barberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Barbeiro não encontrado com o email: " + email));


        return new User(barber.getEmail(), barber.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + barber.getRole().name())));
    }
}
