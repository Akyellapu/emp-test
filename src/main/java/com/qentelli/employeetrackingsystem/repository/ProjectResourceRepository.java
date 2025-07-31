package com.qentelli.employeetrackingsystem.repository;

import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.entity.ProjectResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectResourceRepository extends JpaRepository<ProjectResource, Integer> {

    // 🔍 Fetch all resources for a given project
    List<ProjectResource> findByProject(Project project);

    // 🔍 Search resources by ratio pattern (e.g. "40%")
    List<ProjectResource> findByRatioContaining(String ratio);
}
