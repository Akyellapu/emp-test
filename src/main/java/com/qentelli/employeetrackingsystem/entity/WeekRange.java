package com.qentelli.employeetrackingsystem.entity;

import java.time.LocalDate;

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
public class WeekRange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int weekId;
	private LocalDate weekFromDate;
	private LocalDate weekToDate;
//	private boolean isActive = true;
	private boolean softDelete = false;
	
	@ManyToOne
	@JoinColumn(name = "sprint_id")
	private Sprint sprint;
}