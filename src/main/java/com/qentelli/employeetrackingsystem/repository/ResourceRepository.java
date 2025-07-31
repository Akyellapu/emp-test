package com.qentelli.employeetrackingsystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qentelli.employeetrackingsystem.entity.Resource;
import com.qentelli.employeetrackingsystem.entity.ResourceType;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long>{

	// Fetch all resources by ResourceType
    List<Resource> findByResourceType(ResourceType resourceType);

    // Fetch resources by ResourceType and name match (on TechStack or Project name)
    @Query("""
        SELECT r FROM Resource r
        WHERE r.resourceType = :type AND (
              (:type = com.qentelli.employeetrackingsystem.entity.ResourceType.TECH_STACK AND LOWER(r.techStack) LIKE LOWER(CONCAT('%', :name, '%')))
           OR (:type = com.qentelli.employeetrackingsystem.entity.ResourceType.PROJECT AND LOWER(r.project.projectName) LIKE LOWER(CONCAT('%', :name, '%')))
        )
    """)
    List<Resource> findByResourceTypeAndName(@Param("type") ResourceType type, @Param("name") String name);
}


