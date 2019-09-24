package com.hcl.timesheet;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.ResponseEntity;

import com.hcl.timesheet.controller.TimesheetController;
import com.hcl.timesheet.dto.ApproveDto;
import com.hcl.timesheet.dto.ResponseDto;
import com.hcl.timesheet.dto.TimesheetDto;
import com.hcl.timesheet.service.TimesheetService;
import com.hcl.timesheet.utility.TimesheetUtility;

@RunWith(MockitoJUnitRunner.class)
public class TimesheetControllerTest {

	@Mock
	private TimesheetService timesheetService;

	@InjectMocks
	private TimesheetController timesheetController;

	private TimesheetDto timesheetDto = null;
	private ResponseDto responseDto = null;
	private ApproveDto approveDto = null;

	@Before
	public void setUp() {
		timesheetDto = TimesheetDto.builder().employeeId(101).fromDate(LocalDate.now().minusDays(5))
				.toDate(LocalDate.now()).workingHours(9).build();
		responseDto = ResponseDto.builder().status(TimesheetUtility.STATUS_SUCCCESS)
				.message(TimesheetUtility.TIMESHEET_SUCCESS_MESSAGE).build();
		
		List<Integer> timesheetIds = new ArrayList<>();
		timesheetIds.add(1);
		timesheetIds.add(2);
		timesheetIds.add(4);
		
		approveDto = new ApproveDto();
		approveDto.setEmployeeId(101);
		approveDto.setStatus(1);
		approveDto.setTimesheetIds(timesheetIds);
	}
	
	@Test
	public void submitTimesheetTest() {
		Mockito.when(timesheetService.submitTimesheet(timesheetDto)).thenReturn(responseDto);
		ResponseEntity<ResponseDto> actualResponseEntity = timesheetController.submitTimesheet(timesheetDto);
		
		assertEquals(201, actualResponseEntity.getStatusCodeValue());
	}
	
	@Test
	public void approveTimesheetTest() {
		Mockito.when(timesheetService.approveTimesheet(approveDto, 10)).thenReturn(responseDto);
		ResponseEntity<ResponseDto> actualResponseEntity = timesheetController.approveTimesheet(approveDto, 10);
		
		assertEquals(200, actualResponseEntity.getStatusCodeValue());
	}
}
