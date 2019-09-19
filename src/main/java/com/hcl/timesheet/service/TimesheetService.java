package com.hcl.timesheet.service;

import com.hcl.timesheet.dto.ApproveDto;
import com.hcl.timesheet.dto.ResponseDto;
import com.hcl.timesheet.dto.TimesheetDto;

public interface TimesheetService {

	ResponseDto submitTimesheet(TimesheetDto timesheetDto);

	ResponseDto approveTimesheet(ApproveDto approveDto, Integer managerId);

}
