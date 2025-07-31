package com.qentelli.employeetrackingsystem.models.client.response;

import java.util.List;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.qentelli.employeetrackingsystem.config.FlexibleTechStackDeserializer;
import com.qentelli.employeetrackingsystem.entity.Roles;
import com.qentelli.employeetrackingsystem.entity.TechStack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonDtoResponse {

	private Integer personId;
	private String firstName;
	private String lastName;
	private String email;
	private String employeeCode;
	private String password;
	private String confirmPassword;
	private Roles role;
	@JsonDeserialize(using = FlexibleTechStackDeserializer.class)
	private TechStack techStack;

	private List<Integer> projectIds; // Existing mapping by ID
	private List<String> projectNames;
	private ResourceResponse resource; // Assuming ResourceResponse is defined similarly to PersonRequest
}
