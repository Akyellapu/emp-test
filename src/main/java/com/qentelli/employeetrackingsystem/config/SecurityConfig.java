package com.qentelli.employeetrackingsystem.config;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.qentelli.employeetrackingsystem.entity.CustomUserDetails;
import com.qentelli.employeetrackingsystem.serviceImpl.PersonUserDetailsService;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final PersonUserDetailsService personUserDetailsService;

	@Bean
	public UrlBasedCorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.setAllowedOriginPatterns(Collections.singletonList("http://localhost:4200"));
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.cors(cors -> cors.configurationSource(corsConfigurationSource())).csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(auth -> auth.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers("/auth/register", "/auth/login").permitAll().anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	@Bean
	public UserDetailsService inMemoryUserDetailsService() {
		CustomInMemoryUserDetailsManager manager = new CustomInMemoryUserDetailsManager();

		manager.createUser(new CustomUserDetails("anil@qenteklli.com", passwordEncoder().encode("Anil1996@@"),
				"SUPERADMIN", "Anil", "Kumar"));

		manager.createUser(new CustomUserDetails("madhu@qentelli.com", passwordEncoder().encode("madhu1996@@"), "SUPERADMIN",
				"Madhu", "Marrapu"));

		return manager;
	}

	@SuppressWarnings("deprecation")
	@Bean
	public AuthenticationManager authenticationManager() {
		DaoAuthenticationProvider inMemoryProvider = new DaoAuthenticationProvider();
		inMemoryProvider.setUserDetailsService(inMemoryUserDetailsService());
		inMemoryProvider.setPasswordEncoder(passwordEncoder());

		DaoAuthenticationProvider personProvider = new DaoAuthenticationProvider();
		personProvider.setUserDetailsService(personUserDetailsService);
		personProvider.setPasswordEncoder(passwordEncoder());

		return new ProviderManager(List.of(inMemoryProvider, personProvider));
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}