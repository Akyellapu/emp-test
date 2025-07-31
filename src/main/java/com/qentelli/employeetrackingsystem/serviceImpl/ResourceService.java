package com.qentelli.employeetrackingsystem.serviceImpl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.entity.Resource;
import com.qentelli.employeetrackingsystem.entity.ResourceType;
import com.qentelli.employeetrackingsystem.exception.ResourceNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.ResourceRequest;
import com.qentelli.employeetrackingsystem.models.client.response.ResourceResponse;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.repository.ResourceRepository;

@Service
@Transactional
public class ResourceService {

    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    private final ResourceRepository repository;
    private final ProjectRepository projectRepository;

    public ResourceService(@Lazy ResourceRepository repository, ProjectRepository projectRepository) {
        this.repository = repository;
        this.projectRepository = projectRepository;
    }

    // 🟢 Create
    public ResourceResponse createResource(ResourceRequest dto) {
        logger.info("Creating new Resource with type: {}", dto.getResourceType());

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + dto.getProjectId()));

        Resource resource = new Resource();
        resource.setResourceType(dto.getResourceType());
        resource.setTechStack(dto.getTechStack());
        resource.setProject(project);
        resource.setOnsite(dto.getOnsite());
        resource.setOffsite(dto.getOffsite());
        resource.setRatio(calculateRatio(dto.getOnsite(), dto.getOffsite()));

        Resource saved = repository.save(resource);
        logger.info("Resource saved with ID: {}", saved.getResourceId());

        return mapToResponseDto(saved);
    }

    // 📦 Get All
    public List<ResourceResponse> getAllResources() {
        logger.info("Fetching all Resource entries");
        return repository.findAll()
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // 📄 Paginated Read
    public Page<ResourceResponse> getAllResourcesPaginated(Pageable pageable) {
        logger.info("Fetching resources with pageable: {}", pageable);
        return repository.findAll(pageable).map(this::mapToResponseDto);
    }

    // 🔍 Get by ID
    public ResourceResponse getById(Long id) {
        Resource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));
        return mapToResponseDto(resource);
    }

    // 🔍 Get by ResourceType
    public List<ResourceResponse> getResourcesByType(ResourceType type) {
        logger.info("Fetching resources filtered by type: {}", type);
        return repository.findByResourceType(type)
                .stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // 🔍 Get by ResourceType and Name
    public List<ResourceResponse> getResourcesByTypeAndName(ResourceType type, String name) {
        logger.info("Searching resources by type: {} and name: {}", type, name);

        String processedName = type == ResourceType.TECH_STACK && name != null
                ? name.trim().toUpperCase()
                : name;

        List<Resource> resources = repository.findByResourceTypeAndName(type, processedName);

        if (resources.isEmpty()) {
            logger.warn("No resources found for type '{}' with name '{}'", type, name);
            throw new ResourceNotFoundException("No resources found for type '" + type + "' and name '" + name + "'");
        }

        return resources.stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // 🔄 Update
    public ResourceResponse updateResource(Long id, ResourceRequest dto) {
        logger.info("Updating Resource with ID: {}", id);

        Resource resource = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with ID: " + id));

        Project project = projectRepository.findById(dto.getProjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Project not found with ID: " + dto.getProjectId()));

        resource.setResourceType(dto.getResourceType());
        resource.setTechStack(dto.getTechStack());
        resource.setProject(project);
        resource.setOnsite(dto.getOnsite());
        resource.setOffsite(dto.getOffsite());
        resource.setRatio(calculateRatio(dto.getOnsite(), dto.getOffsite()));

        Resource updated = repository.save(resource);
        logger.info("Resource updated successfully with ID: {}", updated.getResourceId());

        return mapToResponseDto(updated);
    }

    // 🗑️ Delete
    public void deleteResource(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Cannot delete. Resource not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Resource deleted with ID: {}", id);
    }

    // 🧮 Ratio Calculation
    private String calculateRatio(int onsite, int offsite) {
        int total = onsite + offsite;
        if (total == 0) return "0% : 0%";
        int onsiteRatio = (int) Math.round((onsite * 100.0) / total);
        int offsiteRatio = 100 - onsiteRatio;
        return onsiteRatio + "% : " + offsiteRatio + "%";
    }

    // 🔄 DTO Mapper
    private ResourceResponse mapToResponseDto(Resource entity) {
        Project project = entity.getProject();
        return new ResourceResponse(
                entity.getResourceId(),
                entity.getResourceType(),
                entity.getTechStack(),
                project != null ? project.getProjectId() : null,
                project != null ? project.getProjectName() : "N/A",
                entity.getOnsite(),
                entity.getOffsite(),
                entity.getTotal(),
                entity.getTotalOnsiteCount(),
                entity.getTotalOffsiteCount(),
                entity.getTotalRatio(),
                entity.getRatio());
    }
}