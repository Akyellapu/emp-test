package com.qentelli.employeetrackingsystem.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.entity.CustomUserDetails;
import com.qentelli.employeetrackingsystem.repository.PersonRepository;

@Service
public class PersonUserDetailsService implements UserDetailsService {

    @Autowired
    private PersonRepository personRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return personRepository.findByEmail(username)
            .filter(person -> Boolean.TRUE.equals(person.getPersonStatus()))
            .map(person -> new CustomUserDetails(
                person.getEmail(),
                person.getPassword(),
                person.getRole().name(),
                person.getFirstName(),
                person.getLastName()
            ))
            .orElseThrow(() -> {
                return new UsernameNotFoundException("No active Person found: " + username);
            });    }
}