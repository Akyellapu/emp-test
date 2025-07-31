package com.qentelli.employeetrackingsystem.models.client.request;

import com.qentelli.employeetrackingsystem.entity.ResourceType;
import com.qentelli.employeetrackingsystem.entity.TechStack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceRequest {

    private ResourceType resourceType;

    // Used only when resourceType is TECH_STACK
    private TechStack techStack;

    // Used only when resourceType is PROJECT
    private Integer projectId;

    private int onsite;
    private int offsite;
}