package com.qentelli.employeetrackingsystem.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.qentelli.employeetrackingsystem.serviceImpl.PersonUserDetailsService;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	@Lazy
	private CustomInMemoryUserDetailsManager inMemoryUserDetailsManager;

	@Autowired
	@Lazy
	private PersonUserDetailsService personUserDetailsService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		String path = request.getRequestURI();
		return path.startsWith("/auth");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		System.out.println("JwtFilter processing path=" + request.getRequestURI());
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String token = authHeader.substring(7);
			String email = jwtUtil.extractUsername(token);

			if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				try {
					UserDetails userDetails = getUserDetailsByEmail(email);

					if (jwtUtil.validateToken(token)) {
						UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
								userDetails, null, userDetails.getAuthorities());
						authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
						SecurityContextHolder.getContext().setAuthentication(authToken);
					}
				} catch (UsernameNotFoundException ex) {
					System.out.println("User [" + email + "] not found in either source.");
				}
			}
		}

		filterChain.doFilter(request, response);
	}

	private UserDetails getUserDetailsByEmail(String email) {
		try {
			UserDetails user = inMemoryUserDetailsManager.loadUserByUsername(email);
			System.out.println("User [" + email + "] authenticated via IN-MEMORY");
			return user;
		} catch (UsernameNotFoundException e) {
			UserDetails user = personUserDetailsService.loadUserByUsername(email);
			System.out.println("User [" + email + "] authenticated via DATABASE");
			return user;
		}
	}
}