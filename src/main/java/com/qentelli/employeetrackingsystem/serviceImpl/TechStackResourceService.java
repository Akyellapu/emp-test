package com.qentelli.employeetrackingsystem.serviceImpl;

import com.qentelli.employeetrackingsystem.entity.TechStackResource;
import com.qentelli.employeetrackingsystem.exception.TechStackResourceNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.TechStackResourceRequestDto;
import com.qentelli.employeetrackingsystem.models.client.response.TechStackResourceResponseDto;
import com.qentelli.employeetrackingsystem.repository.TechStackResourceRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TechStackResourceService {

    private static final Logger logger = LoggerFactory.getLogger(TechStackResourceService.class);

    private final TechStackResourceRepository repository;

    // 🟢 Create Resource
    public TechStackResourceResponseDto createResource(TechStackResourceRequestDto dto) {
        logger.info("Creating resource entry for TechStack: {}", dto.getTechStack());

        TechStackResource resource = new TechStackResource();
        resource.setTechStack(dto.getTechStack());
        resource.setOnsite(dto.getOnsite());
        resource.setOffsite(dto.getOffsite());
        resource.setRatio(calculateRatio(dto.getOnsite(), dto.getOffsite()));

        // 🔃 First save the entity
        TechStackResource saved = repository.save(resource);

        // 🌐 Inject global summary fields (after save to include the new row in summary)
        injectGlobalSummaryFields(saved);

        // 💾 Save again to persist global fields
        TechStackResource enriched = repository.save(saved);

        logger.info("Resource saved with ID: {}", enriched.getResourceId());
        return mapToResponseDto(enriched);
    }

    // 📦 Get All Resources
    public List<TechStackResourceResponseDto> getAllResources() {
        logger.info("Fetching all TechStack resource entries");
        return repository.findAll().stream()
                .map(this::mapToResponseDto)
                .toList();
    }

    // 🔍 Get by TechStack Name
//    public List<TechStackResourceResponseDto> getByTechStack(String techStackName) {
//        logger.info("Fetching resources for TechStack: {}", techStackName);
//        try {
//            var stack = Enum.valueOf(com.qentelli.employeetrackingsystem.entity.TechStack.class, techStackName);
//            return repository.findByTechStack(stack).stream()
//                    .map(this::mapToResponseDto)
//                    .toList();
//        } catch (IllegalArgumentException ex) {
//            logger.warn("Invalid TechStack requested: {}", techStackName);
//            return List.of();
//        }
//    }
    
    // 🔎 Get by Resource ID
    public TechStackResourceResponseDto getByResourceId(Long resourceId) {
        logger.info("Fetching resource for ID: {}", resourceId);
        return repository.findById(resourceId)
            .map(this::mapToResponseDto)
            .orElseThrow(() -> new TechStackResourceNotFoundException("Resource not found for ID: " + resourceId));
    }

    // 🔄 Update Resource
    public TechStackResourceResponseDto updateResource(Long id, TechStackResourceRequestDto dto) {
        TechStackResource resource = repository.findById(id)
                .orElseThrow(() -> new TechStackResourceNotFoundException("Resource not found with ID: " + id));

        resource.setTechStack(dto.getTechStack());
        resource.setOnsite(dto.getOnsite());
        resource.setOffsite(dto.getOffsite());
        resource.setRatio(calculateRatio(dto.getOnsite(), dto.getOffsite()));

        // 🌐 Update global summary
        injectGlobalSummaryFields(resource);

        TechStackResource updated = repository.save(resource);
        logger.info("Resource updated with ID: {}", updated.getResourceId());

        return mapToResponseDto(updated);
    }

    // 🗑️ Delete Resource
    public void deleteResource(Long id) {
        if (!repository.existsById(id)) {
            throw new TechStackResourceNotFoundException("Cannot delete. Resource not found with ID: " + id);
        }
        repository.deleteById(id);
        logger.info("Resource deleted with ID: {}", id);

        // Optional: Recalculate and batch update global summary on remaining records
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
    private void injectGlobalSummaryFields(TechStackResource resource) {
        List<TechStackResource> all = repository.findAll();

        int totalOnsite = all.stream().mapToInt(TechStackResource::getOnsite).sum();
        int totalOffsite = all.stream().mapToInt(TechStackResource::getOffsite).sum();
        int combined = totalOnsite + totalOffsite;

        String globalRatio = combined == 0
                ? "0% : 0%"
                : Math.round((totalOnsite * 100.0) / combined) + "% : " + (100 - Math.round((totalOnsite * 100.0) / combined)) + "%";

        resource.setTotalOnsiteCount(totalOnsite);
        resource.setTotalOffsiteCount(totalOffsite);
        resource.setTotalRatio(globalRatio);
    }

    // 🧩 Mapper
    private TechStackResourceResponseDto mapToResponseDto(TechStackResource entity) {
        return new TechStackResourceResponseDto(
                entity.getResourceId(),
                entity.getTechStack(),
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