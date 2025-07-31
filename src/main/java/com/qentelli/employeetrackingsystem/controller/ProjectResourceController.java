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
import com.qentelli.employeetrackingsystem.models.client.request.ProjectResourceRequestDto;
import com.qentelli.employeetrackingsystem.models.client.response.AuthResponse;
import com.qentelli.employeetrackingsystem.models.client.response.ListContentWrapper;
import com.qentelli.employeetrackingsystem.models.client.response.ProjectResourceResponseDto;
import com.qentelli.employeetrackingsystem.serviceImpl.ProjectResourceService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/project-resources")
@RequiredArgsConstructor
public class ProjectResourceController {

	private static final Logger logger = LoggerFactory.getLogger(ProjectResourceController.class);

	private final ProjectResourceService service;

	// 🟢 Create Resource
	@PostMapping("/save")
	public ResponseEntity<AuthResponse<Void>> createResource(@RequestBody ProjectResourceRequestDto dto) {
		service.createResource(dto);
		return new ResponseEntity<>(
				new AuthResponse<>(201, RequestProcessStatus.SUCCESS, "Project resource created successfully"),
				HttpStatus.CREATED);
	}

	// ✅ Read All
	@GetMapping("/list")
	public ResponseEntity<AuthResponse<ListContentWrapper<ProjectResourceResponseDto>>> getAllResources() {
		logger.info("Fetching all project resources");
		List<ProjectResourceResponseDto> resources = service.getAllResources();

		AuthResponse<ListContentWrapper<ProjectResourceResponseDto>> response = new AuthResponse<>(
				HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(),
				"Resources fetched successfully", new ListContentWrapper<>(resources.size(), resources));

		return ResponseEntity.ok(response);
	}

	// 🔍 Get by Project Reference
//    @GetMapping("/get-by-project/{projectId}")
//    public ResponseEntity<AuthResponse<List<ProjectResourceResponseDto>>> getByProject(@PathVariable Integer projectId) {
//        Project project = projectService.getById(projectId); // Assuming you have a method to fetch Project by ID
//        List<ProjectResourceResponseDto> resources = projectResourceService.getByProject(project);
//
//        return ResponseEntity.ok(
//            new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Resources fetched successfully", resources)
//        );
//    }

	// 🔍 Get by Resource ID
	@GetMapping("/get/{id}")
	public ResponseEntity<AuthResponse<ListContentWrapper<ProjectResourceResponseDto>>> getById(
			@PathVariable Integer id) {
		logger.info("Fetching ProjectResource by ID: {}", id);

		ProjectResourceResponseDto dto = service.getById(id);

		List<ProjectResourceResponseDto> dtoList = Collections.singletonList(dto); // ✅ Wrap in list

		String message = "Project resource fetched successfully for ID " + id;

		AuthResponse<ListContentWrapper<ProjectResourceResponseDto>> response = new AuthResponse<>(
				HttpStatus.OK.value(), RequestProcessStatus.SUCCESS, LocalDateTime.now(), message,
				new ListContentWrapper<>(dtoList.size(), dtoList));

		return ResponseEntity.ok(response);
	}

	// 🔄 Update Resource
	@PutMapping("/update/{id}")
	public ResponseEntity<AuthResponse<Void>> updateResource(@PathVariable Integer id,
			@RequestBody ProjectResourceRequestDto dto) {
		service.updateResource(id, dto);
		return ResponseEntity
				.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Project resource updated successfully"));
	}

	// 🗑️ Delete Resource
	@DeleteMapping("/delete/{id}")
	public ResponseEntity<AuthResponse<Void>> deleteResource(@PathVariable Integer id) {
		service.deleteResource(id);
		return ResponseEntity
				.ok(new AuthResponse<>(200, RequestProcessStatus.SUCCESS, "Project resource deleted successfully"));
	}
}