package com.qentelli.employeetrackingsystem.models.client.response;

import com.qentelli.employeetrackingsystem.entity.ResourceType;
import com.qentelli.employeetrackingsystem.entity.TechStack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResourceResponse {

	private Long resourceId;
    private ResourceType resourceType;
    private TechStack techStack;
    private Integer projectId;
    private String projectName;
    private int onsite;
    private int offsite;
    private int total;               
    private int totalOnsiteCount;   
    private int totalOffsiteCount;  
    private String totalRatio;      
    private String ratio; 
}


