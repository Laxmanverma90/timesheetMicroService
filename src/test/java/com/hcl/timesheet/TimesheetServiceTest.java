package com.hcl.timesheet;

import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.hcl.timesheet.dto.ApproveDto;
import com.hcl.timesheet.dto.EmployeeDto;
import com.hcl.timesheet.dto.ResponseDto;
import com.hcl.timesheet.dto.TimesheetDto;
import com.hcl.timesheet.entity.Timesheet;
import com.hcl.timesheet.repository.TimesheetRepository;
import com.hcl.timesheet.service.impl.TimesheetServiceImpl;
import com.hcl.timesheet.utility.TimesheetUtility;

@RunWith(MockitoJUnitRunner.class)
public class TimesheetServiceTest {

	@Mock
	private TimesheetUtility timesheetUtility;
	
	@Mock
	private TimesheetRepository timesheetRepository;

	@InjectMocks
	private TimesheetServiceImpl timesheetServiceImpl;

	private TimesheetDto timesheetDto = null;
	private List<LocalDate> holidayDtos = new ArrayList<>();
	private Optional<Timesheet> optionalTimesheet = null;
	private EmployeeDto employeeDto = null;

	private ApproveDto approveDto = null;
	private ApproveDto approveDto1 = null;
	private Optional<List<Timesheet>> timesheetOptional = null;
	private Optional<List<Timesheet>> timesheetOptional1 = null;
	
	@Before
	public void setUp() {
		timesheetDto = TimesheetDto.builder().employeeId(101).fromDate(LocalDate.now().minusDays(5))
				.toDate(LocalDate.now()).workingHours(9).build();
		holidayDtos.add(LocalDate.now().plusDays(4));
		holidayDtos.add(LocalDate.now().plusDays(5));

		optionalTimesheet = Optional.empty();

		employeeDto = new EmployeeDto(101, 10, "Failed", "Failed");
		
		List<Integer> timesheetIds = new ArrayList<>();
		timesheetIds.add(1);
		timesheetIds.add(2);
		timesheetIds.add(4);
		
		approveDto = new ApproveDto();
		approveDto.setEmployeeId(101);
		approveDto.setStatus(1);
		approveDto.setTimesheetIds(timesheetIds);
		
		List<Timesheet> timesheets = new ArrayList<>();
		timesheets.add(Timesheet.builder().timesheetId(1).employeeId(101).reportingManagerId(10).build());
		timesheets.add(Timesheet.builder().timesheetId(2).employeeId(101).reportingManagerId(10).build());
		timesheets.add(Timesheet.builder().timesheetId(4).employeeId(101).reportingManagerId(10).build());
		
		timesheetOptional = Optional.of(timesheets);
		
		List<Integer> timesheetIds1 = new ArrayList<>();
		timesheetIds1.add(1);
		timesheetIds1.add(2);
		timesheetIds1.add(4);
		
		approveDto1 = new ApproveDto();
		approveDto1.setEmployeeId(101);
		approveDto1.setStatus(2);
		approveDto1.setRejectMessage("Invalide working hours");
		approveDto1.setTimesheetIds(timesheetIds1);
		
		timesheetOptional1 = Optional.of(timesheets);
	}

	@Test
	public void submitTimesheetTest() {

		Mockito.when(timesheetUtility.webServiceCall(Mockito.anyString())).thenReturn(holidayDtos);
		Mockito.when(timesheetRepository.findByForDateAndEmployeeId(timesheetDto.getFromDate(),
				timesheetDto.getEmployeeId())).thenReturn(optionalTimesheet);
		Mockito.when(timesheetUtility.getEmployeeManager(101)).thenReturn(employeeDto);
		Mockito.when(timesheetRepository.saveAll(Mockito.anyList())).thenReturn(Mockito.anyList());
		ResponseDto actualResponseDto = timesheetServiceImpl.submitTimesheet(timesheetDto);
		assertEquals(TimesheetUtility.STATUS_SUCCCESS, actualResponseDto.getStatus());
	}
	
	@Test
	public void approveTimesheetAcceptTest() {
		Mockito.when(timesheetRepository.findByEmployeeIdAndReportingManagerIdAndTimesheetIdIn(101, 10,	approveDto.getTimesheetIds())).thenReturn(timesheetOptional);
		Mockito.when(timesheetRepository.saveAll(timesheetOptional.get())).thenReturn(timesheetOptional.get());
		
		ResponseDto actualResult = timesheetServiceImpl.approveTimesheet(approveDto, 10);
		assertEquals(TimesheetUtility.STATUS_SUCCCESS, actualResult.getStatus());
	}
	
	@Test
	public void approveTimesheetRejectTest() {
		Mockito.when(timesheetRepository.findByEmployeeIdAndReportingManagerIdAndTimesheetIdIn(101, 10,	approveDto1.getTimesheetIds())).thenReturn(timesheetOptional1);
		Mockito.when(timesheetRepository.saveAll(timesheetOptional1.get())).thenReturn(timesheetOptional1.get());
		
		ResponseDto actualResult = timesheetServiceImpl.approveTimesheet(approveDto1, 10);
		assertEquals(TimesheetUtility.STATUS_SUCCCESS, actualResult.getStatus());
	}
}


