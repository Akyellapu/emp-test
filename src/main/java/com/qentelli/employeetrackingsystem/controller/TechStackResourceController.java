package com.qentelli.employeetrackingsystem.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.TechStackResourceRequestDto;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ListContentWrapper;
import com.qentelli.employeetrackingsystem.models.client.response.ReleaseResponseDTO;
import com.qentelli.employeetrackingsystem.models.client.response.TechStackResourceResponseDto;
import com.qentelli.employeetrackingsystem.serviceImpl.TechStackResourceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/techstack-resources")
@RequiredArgsConstructor
public class TechStackResourceController {

	private static final Logger logger = LoggerFactory.getLogger(TechStackResourceController.class);

	private final TechStackResourceService service;

	// 🟢 Create Resource
	@PostMapping("/save")
	public ResponseEntity<AuthResponse<Void>> createResource(@RequestBody TechStackResourceRequestDto dto) {
		service.createResource(dto);
		return new ResponseEntity<>(
				new AuthResponse<>(201, RequestProcessStatus.SUCCESS, "Resource created successfully"),
				HttpStatus.CREATED);
	}

	// ✅ Read All
	@GetMapping("/list")
	public ResponseEntity<AuthResponse<ListContentWrapper<TechStackResourceResponseDto>>> getAllResources() {
		logger.info("Fetching all TechStack resources");

		List<TechStackResourceResponseDto> resources = service.getAllResources();

		ListContentWrapper<TechStackResourceResponseDto> wrapper = new ListContentWrapper<>(resources.size(),
				resources);

		AuthResponse<ListContentWrapper<TechStackResourceResponseDto>> response = new AuthResponse<>(
				HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Resources fetched successfully", wrapper);

		return ResponseEntity.ok(response);
	}

	// ✅ Read by TechStack Name
//	@GetMapping("/stack/{techStackName}")
//	public ResponseEntity<AuthResponse<ListContentWrapper<TechStackResourceResponseDto>>> getByTechStack(
//			@PathVariable String techStackName) {
//
//		logger.info("Fetching resources for techStack: {}", techStackName);
//		List<TechStackResourceResponseDto> resources = service.getByTechStack(techStackName);
//
//		String message = resources.isEmpty() ? "No resources found for TechStack: " + techStackName
//				: "Resources fetched successfully for TechStack: " + techStackName;
//
//		AuthResponse<ListContentWrapper<TechStackResourceResponseDto>> response = new AuthResponse<>(
//				HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), message,
//				new ListContentWrapper<>(resources.size(), resources));
//
//		return ResponseEntity.ok(response);
//	}
//	
	// 🔍 Get by Resource ID
	@GetMapping("/get/{id}")
	public ResponseEntity<AuthResponse<ListContentWrapper<TechStackResourceResponseDto>>> getByResourceId(@PathVariable Long id) {
	    logger.info("Received request to fetch TechStack resource with ID: {}", id);

	    TechStackResourceResponseDto resource = service.getByResourceId(id);

	    List<TechStackResourceResponseDto> resourceList = Collections.singletonList(resource); // ✅ Convert to list

	    String message = "Resource fetched successfully for given Resource ID " + id;

	    AuthResponse<ListContentWrapper<TechStackResourceResponseDto>> response = new AuthResponse<>(
	        HttpStatus.OK.value(),
	        RequestProcessStatus.SUCCESS,
	        LocalDateTime.now(),
	        message,
	        new ListContentWrapper<>(resourceList.size(), resourceList)
	    );

	    return ResponseEntity.ok(response);
	}

	// 🔄 Update Resource
	@PutMapping("/update/{id}")
	public ResponseEntity<AuthResponse<Void>> updateResource(@PathVariable Long id,
			@RequestBody TechStackResourceRequestDto dto) {
		service.updateResource(id, dto);
		return ResponseEntity
				.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Resource updated successfully"));
	}

	// 🗑️ Delete Resource
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<AuthResponse<Void>> deleteResource(@PathVariable Long id) {
		service.deleteResource(id);
		return ResponseEntity
				.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Resource deleted successfully"));
	}

}