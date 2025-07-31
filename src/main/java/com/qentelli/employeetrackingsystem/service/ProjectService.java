package com.qentelli.employeetrackingsystem.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.qentelli.employeetrackingsystem.exception.DuplicateProjectException;
import com.qentelli.employeetrackingsystem.models.client.request.ProjectDTO;
import com.qentelli.employeetrackingsystem.entity.Project;

public interface ProjectService {

    ProjectDTO create(ProjectDTO dto) throws DuplicateProjectException;

    List<ProjectDTO> getAll();

    ProjectDTO update(Integer id, ProjectDTO dto);

    void deleteProject(Integer projectId);

    Page<Project> searchProjectsByExactName(String name, Pageable pageable);

    Page<Project> getactiveProjects(Pageable pageable);
}