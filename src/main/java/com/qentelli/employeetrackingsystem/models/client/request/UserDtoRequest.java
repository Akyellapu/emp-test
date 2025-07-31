package com.qentelli.employeetrackingsystem.models.client.request;

import java.util.List;

import lombok.Data;

@Data
public class UserDtoRequest {
	private int id;
	private String firstName;
	private String lastName;
	private String employeeId;
	private String email;
	private String password;
	private String confirmPassword;
	private String roles;
	private String techStack;
	private List<ProjectRequest> projects;
}
