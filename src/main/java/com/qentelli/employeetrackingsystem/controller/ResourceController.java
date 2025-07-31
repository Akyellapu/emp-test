package com.qentelli.employeetrackingsystem.controller;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
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

import com.qentelli.employeetrackingsystem.entity.ResourceType;
import com.qentelli.employeetrackingsystem.exception.RequestProcessStatus;
import com.qentelli.employeetrackingsystem.models.client.request.ResourceRequest;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ListContentWrapper;
import com.qentelli.employeetrackingsystem.models.client.response.PaginatedResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ResourceResponse;
import com.qentelli.employeetrackingsystem.serviceImpl.ResourceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/resources")
@RequiredArgsConstructor
public class ResourceController {

	private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);
	
	@Lazy // Avoid circular dependency issues
	private final ResourceService service;
	// 🟢 Create Resource
	@PostMapping("/save")
	public ResponseEntity<AuthResponse<Void>> createResource(@RequestBody ResourceRequest dto) {
		service.createResource(dto);
		return new ResponseEntity<>(
				new AuthResponse<>(201, RequestProcessStatus.SUCCESS, "Resource created successfully"),
				HttpStatus.CREATED);
	}

	// 📦 Read All Resources
	@GetMapping("/list")
	public ResponseEntity<AuthResponse<ListContentWrapper<ResourceResponse>>> getAllResources() {
		logger.info("Fetching all resources");
		List<ResourceResponse> resources = service.getAllResources();

		AuthResponse<ListContentWrapper<ResourceResponse>> response = new AuthResponse<>(
				HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Resources fetched successfully", new ListContentWrapper<>(resources.size(), resources));

		return ResponseEntity.ok(response);
	}
	
	// 📄 Read all by Paginated Read
	@GetMapping("/paginated")
	public ResponseEntity<AuthResponse<PaginatedResponse<ResourceResponse>>> getAllResourcesPaginated(
	        @RequestParam(defaultValue = "0") int page,
	        @RequestParam(defaultValue = "5") int size,
	        @RequestParam(defaultValue = "resourceType") String sortBy
	) {
	    logger.info("Fetching paginated resources: page={}, size={}, sortBy={}", page, size, sortBy);
	    Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
	    Page<ResourceResponse> resourcePage = service.getAllResourcesPaginated(pageable);

	    PaginatedResponse<ResourceResponse> paginated = new PaginatedResponse<>(
	            resourcePage.getContent(),
	            resourcePage.getNumber(),
	            resourcePage.getSize(),
	            resourcePage.getTotalElements(),
	            resourcePage.getTotalPages(),
	            resourcePage.isLast()
	    );

	    logger.debug("Paginated resources fetched: count={}, totalPages={}", resourcePage.getNumberOfElements(), resourcePage.getTotalPages());

	    AuthResponse<PaginatedResponse<ResourceResponse>> response = new AuthResponse<>(
	            HttpStatus.OK.value(),
	            RequestProcessStatus.SUCCESS,
	            LocalDateTime.now(),
	            "Paginated resources fetched successfully",
	            paginated
	    );

	    return ResponseEntity.ok(response);
	}

	// 🔍 Get Resource by ID
	@GetMapping("/get/{id}")
	public ResponseEntity<AuthResponse<ListContentWrapper<ResourceResponse>>> getById(@PathVariable Long id) {
		logger.info("Fetching resource by ID: {}", id);

		ResourceResponse dto = service.getById(id);
		List<ResourceResponse> dtoList = Collections.singletonList(dto); // 🔄 Wrap single DTO into List

		String message = "Resource fetched successfully for ID " + id;

		AuthResponse<ListContentWrapper<ResourceResponse>> response = new AuthResponse<>(
				HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), message,
				new ListContentWrapper<>(dtoList.size(), dtoList));

		return ResponseEntity.ok(response);
	}
	
	// 🔍 Filter by Resource Type (TECH_STACK or PROJECT)
	@GetMapping("/filter-by-type")
	public ResponseEntity<AuthResponse<ListContentWrapper<ResourceResponse>>> getByType(
	        @RequestParam ResourceType type) {

	    logger.info("Fetching resources filtered by type: {}", type);
	    List<ResourceResponse> resources = service.getResourcesByType(type);

	    AuthResponse<ListContentWrapper<ResourceResponse>> response = new AuthResponse<>(
	            HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(),
	            "Resources filtered by type: " + type, new ListContentWrapper<>(resources.size(), resources));

	    return ResponseEntity.ok(response);
	}
	
	// 🔍 Filter by Type and Name (e.g., TECH_STACK + FULLSTACK or PROJECT + INITIO)
	@GetMapping("/filter-by-type-and-name")
	public ResponseEntity<AuthResponse<ListContentWrapper<ResourceResponse>>> getByTypeAndName(
	        @RequestParam ResourceType type,
	        @RequestParam String name) {

	    logger.info("Fetching resources by type: {} and name: {}", type, name);
	    List<ResourceResponse> resources = service.getResourcesByTypeAndName(type, name);

	    AuthResponse<ListContentWrapper<ResourceResponse>> response = new AuthResponse<>(
	            HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(),
	            "Resources filtered by type: " + type + " and name: " + name,
	            new ListContentWrapper<>(resources.size(), resources));

	    return ResponseEntity.ok(response);
	}

	// 🔄 Update Resource
	@PutMapping("/update/{id}")
	public ResponseEntity<AuthResponse<Void>> updateResource(@PathVariable Long id, @RequestBody ResourceRequest dto) {
		service.updateResource(id, dto);
		return ResponseEntity.ok(
				new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Resource updated successfully"));
	}

	// 🗑️ Delete Resource
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<AuthResponse<Void>> deleteResource(@PathVariable Long id) {
		service.deleteResource(id);
		return ResponseEntity.ok(
				new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Resource deleted successfully"));
	}
}
