package com.qentelli.employeetrackingsystem.models.client.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReleaseRequestDTO {

	private Long releaseId;
	
	@NotNull(message = "Week ID is required")
	private Integer weekId;
	
	@NotNull(message = "Project ID is required")
    private Integer projectId;
	
	@NotNull(message = "Sprint ID is required")
	private long sprintId;
	
	@NotNull(message = "Major count is required")
    private int major;
    
	@NotNull(message = "Minor count is required")
    private int minor;
    
    private int incidentCreated;
    private String releaseInformation;

}
