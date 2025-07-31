package com.qentelli.employeetrackingsystem.models.client.response;

import com.qentelli.employeetrackingsystem.entity.TechStack;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TechStackResourceResponseDto {

    private Long resourceId;

    private TechStack techStack;

    private int onsite;
    private int offsite;

    private int total; // Optional: derived from onsite + offsite
    
    private String ratio;
    
    private int totalOnsiteCount;
    private int totalOffsiteCount;
    private String totalRatio;
}
