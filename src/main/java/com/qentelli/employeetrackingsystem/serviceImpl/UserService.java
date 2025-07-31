package com.qentelli.employeetrackingsystem.serviceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.qentelli.employeetrackingsystem.config.JwtUtil;
import com.qentelli.employeetrackingsystem.entity.CustomUserDetails;
import com.qentelli.employeetrackingsystem.models.client.response.LoginUserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final AuthenticationManager authenticationManager;

	private final JwtUtil jwtUtil;

	public LoginUserResponse login(String email, String password) {
		Authentication authentication = authenticationManager
				.authenticate(new UsernamePasswordAuthenticationToken(email, password));

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();

		String token = jwtUtil.generateToken(userDetails.getUsername());

		if (token == null || token.isEmpty()) {
			throw new RuntimeException("Token generation failed");
		}

		LoginUserResponse response = new LoginUserResponse();
		response.setUserName(userDetails.getUsername());
		response.setAcessToken(token);

		if (userDetails instanceof CustomUserDetails custom) {
			response.setFirstName(custom.getFirstName());
			response.setLastName(custom.getLastName());
			response.setRole(custom.getRole());
		} else {
			response.setRole(userDetails.getAuthorities().stream().findFirst().map(GrantedAuthority::getAuthority)
					.orElse("UNKNOWN"));
		}
		return response;
	}

}
