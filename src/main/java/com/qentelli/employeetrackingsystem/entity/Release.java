package com.qentelli.employeetrackingsystem.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Release {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long releaseId;

	@ManyToOne
	@JoinColumn(name = "weekId")
	private WeekRange week;
	
	@ManyToOne
	@JoinColumn(name = "sprintId")
	private Sprint sprint;
	
	@ManyToOne
	@JoinColumn(name = "projectId")
	private Project project;

	private int major;
	private int minor;
	private int incidentCreated;
	private String releaseInformation;

}
