package com.qentelli.employeetrackingsystem.config;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import com.qentelli.employeetrackingsystem.entity.CustomUserDetails;

public class CustomInMemoryUserDetailsManager extends InMemoryUserDetailsManager {

	private final Map<String, CustomUserDetails> enrichedUsers = new ConcurrentHashMap<>();

	public void createUser(CustomUserDetails user) {
		super.createUser(user); // stores basic credentials
		enrichedUsers.put(user.getUsername(), user); // stores full enrichment
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		CustomUserDetails enriched = enrichedUsers.get(username);
		return (enriched != null) ? enriched : (CustomUserDetails) super.loadUserByUsername(username);
	}
}