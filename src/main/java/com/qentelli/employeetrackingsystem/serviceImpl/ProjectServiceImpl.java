package com.qentelli.employeetrackingsystem.serviceImpl;

import java.time.LocalDateTime;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.qentelli.employeetrackingsystem.entity.Account;
import com.qentelli.employeetrackingsystem.entity.CustomUserDetails;
import com.qentelli.employeetrackingsystem.entity.Project;
import com.qentelli.employeetrackingsystem.exception.AccountNotFoundException;
import com.qentelli.employeetrackingsystem.exception.DuplicateProjectException;
import com.qentelli.employeetrackingsystem.exception.ProjectNotFoundException;
import com.qentelli.employeetrackingsystem.models.client.request.ProjectDTO;
import com.qentelli.employeetrackingsystem.repository.AccountRepository;
import com.qentelli.employeetrackingsystem.repository.ProjectRepository;
import com.qentelli.employeetrackingsystem.service.ProjectService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private static final String PROJECT_NOT_FOUND = "Project not found with id : ";
    private static final String ACCOUNT_NOT_FOUND = "Account not found with id : ";

    private final ProjectRepository projectRepo;
    private final AccountRepository accountRepo;
    private final ModelMapper modelMapper;

    @Override
    public ProjectDTO create(ProjectDTO dto) throws DuplicateProjectException {
        if (projectRepo.existsByProjectName(dto.getProjectName())) {
            throw new DuplicateProjectException("A project with this name already exists.");
        }

        Account account = accountRepo.findById(dto.getAccountId())
                .orElseThrow(() -> new AccountNotFoundException(ACCOUNT_NOT_FOUND + dto.getAccountId()));

        Project project = modelMapper.map(dto, Project.class);
        project.setAccount(account);

        Project saved = projectRepo.save(project);
        return modelMapper.map(saved, ProjectDTO.class);
    }

    @Override
    public List<ProjectDTO> getAll() {
        List<Project> projects = projectRepo.findAll();

        return projects.stream().map(project -> {
            ProjectDTO dto = modelMapper.map(project, ProjectDTO.class);
            dto.setAccountName(project.getAccount().getAccountName());
            return dto;
        }).toList();
    }

    @Override
    @Transactional
    public ProjectDTO update(Integer id, ProjectDTO dto) {
        Project project = projectRepo.findById(id)
                .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND + id));

        project.setProjectName(dto.getProjectName());
        project.setUpdatedAt(LocalDateTime.now());
        project.setUpdatedBy(getAuthenticatedUserFullName());

        return modelMapper.map(project, ProjectDTO.class);
    }

    @Override
    @Transactional
    public void deleteProject(Integer projectId) {
        Project project = projectRepo.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(PROJECT_NOT_FOUND + projectId));

        project.setProjectStatus(false);
        project.setUpdatedAt(LocalDateTime.now());
        project.setUpdatedBy(getAuthenticatedUserFullName());

        projectRepo.save(project);
    }

    @Override
    public Page<Project> searchProjectsByExactName(String name, Pageable pageable) {
        return projectRepo.findByProjectNameContainingIgnoreCase(name, pageable);
    }

    @Override
    public Page<Project> getactiveProjects(Pageable pageable) {
        return projectRepo.findByProjectStatusTrue(pageable);
    }

    private String getAuthenticatedUserFullName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();

            if (principal instanceof CustomUserDetails custom) {
                return custom.getFirstName() + " " + custom.getLastName();
            }
        }

        return "System";
    }
}
