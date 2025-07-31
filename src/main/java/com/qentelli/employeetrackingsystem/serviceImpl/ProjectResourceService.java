package com.qentelli.employeetrackingsystem.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qentelli.employeetrackingsystem.entity.ProjectResource;
import com.qentelli.employeetrackingsystem.exception.ProjectResourceNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.ProjectResourceRequestDto;
import com.qentelli.employeetrackingsystem.models.client.response.ProjectResourceResponseDto;
import com.qentelli.employeetrackingsystem.repository.ProjectResourceRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjectResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ProjectResourceService.class);

    private final ProjectResourceRepository repository;

    // 🟢 Create Resource
    public ProjectResourceResponseDto createResource(ProjectResourceRequestDto dto) {
        logger.info("Creating resource for Project: {}", dto.getProject());

        ProjectResource resource = new ProjectResource();
        resource.setProject(dto.getProject());
        resource.setOnsite(dto.getOnsite());
        resource.setOffsite(dto.getOffsite());
        resource.setRatio(calculateRatio(dto.getOnsite(), dto.getOffsite()));

        ProjectResource saved = repository.save(resource);
        injectGlobalSummaryFields(saved);
        ProjectResource enriched = repository.save(saved);

        logger.info("Project resource saved with ID: {}", enriched.getResourceId());
        return mapToResponseDto(enriched);
    }

    // 📦 Get All Resources
    public List<ProjectResourceResponseDto> getAllResources() {
        logger.info("Fetching all Project resource entries");
        return repository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // 🔍 Get by Project Reference
//    public List<ProjectResourceResponseDto> getByProject(Project project) {
//        logger.info("Fetching resources for Project: {}", project.getProjectName());
//        return repository.findByProject(project).stream()
//                .map(this::mapToResponseDto)
//                .toList();
//    }
    
    //Get by Resource ID
    public ProjectResourceResponseDto getById(Integer id) {
        ProjectResource resource = repository.findById(id)
            .orElseThrow(() -> new ProjectResourceNotFoundException("Resource not found with ID: " + id));

        return mapToResponseDto(resource);
    }

    // 🔄 Update Resource
    public ProjectResourceResponseDto updateResource(Integer id, ProjectResourceRequestDto dto) {
        ProjectResource resource = repository.findById(id)
                .orElseThrow(() -> new ProjectResourceNotFoundException("Resource not found with ID: " + id));

        resource.setProject(dto.getProject());
        resource.setOnsite(dto.getOnsite());
        resource.setOffsite(dto.getOffsite());
        resource.setRatio(calculateRatio(dto.getOnsite(), dto.getOffsite()));

        injectGlobalSummaryFields(resource);
        ProjectResource updated = repository.save(resource);

        logger.info("Project resource updated with ID: {}", updated.getResourceId());
        return mapToResponseDto(updated);
    }

    // 🗑️ Delete Resource
    public void deleteResource(Integer id) {
        if (!repository.existsById(id)) {
            throw new ProjectResourceNotFoundException("Cannot delete. Resource not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Project resource deleted with ID: {}", id);
    }

    // 🧮 Ratio Calculation
    private String calculateRatio(int onsite, int offsite) {
        int total = onsite + offsite;
        if (total == 0) {
            return "0% : 0%";
        }
        int onsiteRatio = (int) Math.round((onsite * 100.0) / total);
        int offsiteRatio = 100 - onsiteRatio;
        return onsiteRatio + "% : " + offsiteRatio + "%";
    }

    // 🌐 Global Summary Injector
    private void injectGlobalSummaryFields(ProjectResource resource) {
        List<ProjectResource> all = repository.findAll();

        int totalOnsite = all.stream().mapToInt(ProjectResource::getOnsite).sum();
        int totalOffsite = all.stream().mapToInt(ProjectResource::getOffsite).sum();
        int combined = totalOnsite + totalOffsite;

        String globalRatio = combined == 0
                ? "0% : 0%"
                : Math.round((totalOnsite * 100.0) / combined) + "% : " + (100 - Math.round((totalOnsite * 100.0) / combined)) + "%";

        resource.setTotalOnsiteCount(totalOnsite);
        resource.setTotalOffsiteCount(totalOffsite);
        resource.setTotalRatio(globalRatio);
    }

    // 🧩 Mapper
    private ProjectResourceResponseDto mapToResponseDto(ProjectResource entity) {
        return new ProjectResourceResponseDto(
            entity.getResourceId(),
            entity.getProject().getProjectName(),           // projectName instead of Project object
            entity.getOnsite(),
            entity.getOffsite(),
            entity.getTotal(),
            entity.getRatio(),
            entity.getTotalOnsiteCount(),
            entity.getTotalOffsiteCount(),
            entity.getTotalRatio()
        );
    }
}
