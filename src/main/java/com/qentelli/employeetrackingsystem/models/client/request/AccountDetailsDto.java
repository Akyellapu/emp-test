package com.qentelli.employeetrackingsystem.models.client.request;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class AccountDetailsDto {
	private int accountId;
	@NotBlank(message = "Project name is required")
	@Size(max = 20, message = "Project name must not exceed 20 characters")
	private String accountName;
	private LocalDate accountStartDate;
	private LocalDate accountEndDate;
	private Boolean accountStatus = true; // true means active, false means inactive
	private LocalDateTime createdAt;
	private String createdBy;
	private LocalDateTime updatedAt;
	private String updatedBy;
}