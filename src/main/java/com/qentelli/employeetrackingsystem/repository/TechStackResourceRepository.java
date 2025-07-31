package com.qentelli.employeetrackingsystem.repository;

import com.qentelli.employeetrackingsystem.entity.TechStackResource;
import com.qentelli.employeetrackingsystem.entity.TechStack;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TechStackResourceRepository extends JpaRepository<TechStackResource, Long> {

    // Custom query: get all by tech-stack type
    List<TechStackResource> findByTechStack(TechStack techStack);
    
    List<TechStackResource> findByRatioContaining(String ratio);
}