package com.qentelli.employeetrackingsystem.controller;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.SprintRequest;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.PaginatedResponse;
import com.qentelli.employeetrackingsystem.models.client.response.SprintResponse;
import com.qentelli.employeetrackingsystem.serviceImpl.SprintService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/sprints")
public class SprintController {

	@Autowired
	private SprintService sprintService;

	@PostMapping("/createSprint")
	public ResponseEntity<AuthResponse<Void>> create(@Valid @RequestBody SprintRequest request) {
		sprintService.createSprint1(request); // call service, ignore returned SprintResponse
		return ResponseEntity.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Sprint created successfully", null // no data
		));
	}

	@GetMapping("/getAllSprints")
	public ResponseEntity<AuthResponse<PaginatedResponse<SprintResponse>>> getAll(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "sprintId") // newest (highest id)
																								// first
				.and(Sort.by(Sort.Direction.DESC, "fromDate")) // optional tie-breaker

		);

		Page<SprintResponse> responsePage = sprintService.getAllSprints(pageable);
		PaginatedResponse<SprintResponse> paginatedResponse = new PaginatedResponse<>(responsePage.getContent(),
				responsePage.getNumber(), responsePage.getSize(), responsePage.getTotalElements(),
				responsePage.getTotalPages(), responsePage.isLast());

		AuthResponse<PaginatedResponse<SprintResponse>> authResponse = new AuthResponse<>(HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Sprints fetched successfully", paginatedResponse);
		return ResponseEntity.ok(authResponse);
	}

	@GetMapping("/{id}")
	public ResponseEntity<AuthResponse<SprintResponse>> getById(@PathVariable Long id) {
		SprintResponse response = sprintService.getSprintById(id);
		return ResponseEntity.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Sprint retrieved successfully", response));
	}

	@PutMapping("/{id}")
	public ResponseEntity<AuthResponse<SprintResponse>> update(@PathVariable Long id,
			@Valid @RequestBody SprintRequest request) {
		SprintResponse response = sprintService.updateSprint(id, request);
		return ResponseEntity.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Sprint updated successfully", null));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<AuthResponse<Void>> delete(@PathVariable Long id) {
		sprintService.deleteSprint(id);
		return ResponseEntity.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Sprint deleted successfully", null));
	}

	@PutMapping("/enable/{id}")
	public ResponseEntity<AuthResponse<Void>> enableSprint(@PathVariable Long id) {
		boolean updated = sprintService.setSprintEnabled(id);
		if (updated) {
			return ResponseEntity.ok(new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS,
					LocalDateTime.now(), "Sprint enabled successfully", null));
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new AuthResponse<>(HttpStatus.NOT_FOUND.value(),
					RequestProcessStatus.FAILURE, LocalDateTime.now(), "Sprint not found", null));
		}
	}
}
