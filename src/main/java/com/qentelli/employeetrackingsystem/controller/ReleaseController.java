package com.qentelli.employeetrackingsystem.controller;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.ReleaseRequestDTO;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ListContentWrapper;
import com.qentelli.employeetrackingsystem.models.client.response.PaginatedResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ReleaseResponseDTO;
import com.qentelli.employeetrackingsystem.serviceImpl.ReleaseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/releases")
@RequiredArgsConstructor
public class ReleaseController {

	private static final Logger logger = LoggerFactory.getLogger(ReleaseController.class);

	private final ReleaseService service;

	//Create Releases
	@PostMapping("/save")
	public ResponseEntity<AuthResponse<String>> createRelease(@Valid @RequestBody ReleaseRequestDTO dto) {
		logger.info("Creating new release for project ID {}", dto.getProjectId());
		service.createRelease(dto);
		logger.info("Release created successfully");

		AuthResponse<String> response = new AuthResponse<>(HttpStatus.CREATED.value(), RequestProcessStatus.SUCCESS,
				"Release created successfully.");

		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	// Read all releases
	@GetMapping("/list")
	public ResponseEntity<AuthResponse<ListContentWrapper<ReleaseResponseDTO>>> getAllReleases() {
		logger.info("Fetching all release entries");
		List<ReleaseResponseDTO> releaseList = service.getAllReleases();

		ListContentWrapper<ReleaseResponseDTO> wrapped = new ListContentWrapper<>(releaseList.size(), releaseList);

		AuthResponse<ListContentWrapper<ReleaseResponseDTO>> response = new AuthResponse<>(HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Release data retrieved successfully", wrapped);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	// Read paginated releases
	@GetMapping("/paginated")
	public ResponseEntity<AuthResponse<PaginatedResponse<ReleaseResponseDTO>>> getPaginatedReleases(
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "5") int size,
			@RequestParam(defaultValue = "releaseInformation") String sortBy) {
		logger.info("Fetching paginated releases: page={}, size={}, sortBy={}", page, size, sortBy);
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
		Page<ReleaseResponseDTO> releasePage = service.getPaginatedReleases(pageable);

		PaginatedResponse<ReleaseResponseDTO> paginated = new PaginatedResponse<>(releasePage.getContent(),
				releasePage.getNumber(), releasePage.getSize(), releasePage.getTotalElements(),
				releasePage.getTotalPages(), releasePage.isLast());

		logger.debug("Paginated releases fetched: count={}, totalPages={}", releasePage.getNumberOfElements(),
				releasePage.getTotalPages());

		AuthResponse<PaginatedResponse<ReleaseResponseDTO>> response = new AuthResponse<>(HttpStatus.OK.value(),
				RequestProcessStatus.SUCCESS, LocalDateTime.now(), "Paginated releases fetched successfully",
				paginated);

		return ResponseEntity.ok(response);
	}
	
	// Read by weekId only
	@GetMapping("/week/{weekId}")
	public ResponseEntity<AuthResponse<ListContentWrapper<ReleaseResponseDTO>>> getReleasesByWeekId(
	        @PathVariable int weekId) {
	 
	    logger.info("Fetching releases for week ID {}", weekId);
	 
	    List<ReleaseResponseDTO> releases = service.getReleasesByWeekId(weekId);
	 
	    String message = releases.isEmpty()
	        ? "No releases found for week ID "
	        : "Releases fetched successfully for week ID " + weekId;
	 
	    AuthResponse<ListContentWrapper<ReleaseResponseDTO>> response = new AuthResponse<>(
	        HttpStatus.OK.value(),
	        RequestProcessStatus.SUCCESS,
	        LocalDateTime.now(),
	        message,
	        new ListContentWrapper<>(releases.size(), releases)
	    );
	 
	    return ResponseEntity.ok(response);
	}

	// Read by sprintId only	
	@GetMapping("/sprint/{sprintId}")
	public ResponseEntity<AuthResponse<ListContentWrapper<ReleaseResponseDTO>>> getReleasesBySprintId(
	        @PathVariable int sprintId) {
	 
	    logger.info("Fetching releases for sprint ID {}", sprintId);
	 
	    List<ReleaseResponseDTO> releases = service.getReleasesBySprintId(sprintId);
	 
	    String message = releases.isEmpty()
	        ? "No releases found for sprint ID " + sprintId
	        : "Releases fetched successfully for sprint ID " + sprintId;
	 
	    AuthResponse<ListContentWrapper<ReleaseResponseDTO>> response = new AuthResponse<>(
	        HttpStatus.OK.value(),
	        RequestProcessStatus.SUCCESS,
	        LocalDateTime.now(),
	        message,
	        new ListContentWrapper<>(releases.size(), releases)
	    );
	 
	    return ResponseEntity.ok(response);
	}

	// Update by ID
	@PutMapping("/update/{id}")
	public ResponseEntity<AuthResponse<String>> updateRelease(@PathVariable Long id,
			@Valid @RequestBody ReleaseRequestDTO dto) {
		logger.info("Updating release with ID {}", id);
		service.updateRelease(id, dto);
		logger.info("Release updated successfully");

		AuthResponse<String> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS,
				"Release updated successfully.");

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	// Delete by ID
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<AuthResponse<Void>> deleteRelease(@PathVariable Long id) {
		logger.info("Deleting release with ID {}", id);
		service.deleteRelease(id);
		logger.info("Release deleted successfully");

		AuthResponse<Void> response = new AuthResponse<>(HttpStatus.OK.value(), RequestProcessStatus.SUCCESS,
				LocalDateTime.now(), "Release deleted successfully", null);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
