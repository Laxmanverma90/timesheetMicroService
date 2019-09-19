package com.hcl.timesheet.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HolidayDto {

	private Integer holidaysId;
	private LocalDate holidayDate;
	private String remarks;
	
}