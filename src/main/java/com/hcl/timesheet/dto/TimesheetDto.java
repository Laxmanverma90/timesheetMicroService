package com.hcl.timesheet.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Laxman
 *
 */
@Setter
@Getter
public class TimesheetDto {

	private int employeeId;
	private LocalDate fromDate;
	private LocalDate toDate;
	private Integer workingHours;
}