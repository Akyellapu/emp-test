package com.qentelli.employeetrackingsystem.entity;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

    private final String firstName;
    private final String lastName;
    private final String role;

    public CustomUserDetails(String username, String password, String role, String firstName, String lastName) {
        super(username, password, List.of(new SimpleGrantedAuthority(role)));
        this.role = role;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getRole() { return role; }
}