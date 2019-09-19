package com.hcl.timesheet.dto;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ApproveDto {

	private Integer employeeId;
	private List<Integer> timesheetIds; 
	private Integer status;
	private String rejectMessage;
}
