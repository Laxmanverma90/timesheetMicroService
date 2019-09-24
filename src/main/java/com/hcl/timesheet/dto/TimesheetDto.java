package com.hcl.timesheet.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Laxman
 *
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimesheetDto {

	private int employeeId;
	private LocalDate fromDate;
	private LocalDate toDate;
	private Integer workingHours;
}