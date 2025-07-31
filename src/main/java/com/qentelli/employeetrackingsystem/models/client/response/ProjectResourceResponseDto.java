package com.qentelli.employeetrackingsystem.models.client.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProjectResourceResponseDto {

	private Integer resourceId;

	private String projectName;

	private int onsite;
	private int offsite;

	private int total; // Derived from onsite + offsite

	private String ratio; // e.g. "40% : 60%"

	private int totalOnsiteCount; // Sum across all project resources
	private int totalOffsiteCount; // Sum across all project resources
	private String totalRatio; // e.g. "35% : 65%"
}
