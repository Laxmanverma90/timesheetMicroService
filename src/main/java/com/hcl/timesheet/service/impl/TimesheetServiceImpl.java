package com.hcl.timesheet.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.hcl.timesheet.Entity.Timesheet;
import com.hcl.timesheet.dto.ApproveDto;
import com.hcl.timesheet.dto.EmployeeDto;
import com.hcl.timesheet.dto.HolidayDto;
import com.hcl.timesheet.dto.ResponseDto;
import com.hcl.timesheet.dto.TimesheetDto;
import com.hcl.timesheet.exception.AlreadyTimesheetSubmitted;
import com.hcl.timesheet.exception.HolidayDateException;
import com.hcl.timesheet.exception.InvalidDateException;
import com.hcl.timesheet.exception.RecordNotFoundException;
import com.hcl.timesheet.repository.TimesheetRepository;
import com.hcl.timesheet.service.TimesheetService;
import com.hcl.timesheet.utility.Status;
import com.hcl.timesheet.utility.TimesheetUtility;
import com.hcl.timesheet.utility.WeekEnd;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxman
 * @date 19 SEPT 2019
 */
@Slf4j
@Service
public class TimesheetServiceImpl implements TimesheetService {

	@Autowired
	private TimesheetRepository timesheetRepository;

	@Autowired
	private TimesheetUtility timesheetUtility;

	@Autowired
	private RestTemplate restTemplate;

	@Value("${cant.submit.for.future.date}")
	private String cantSubmitForFutureDate;

	@Value("${toDate.isBefore.fromDate}")
	private String toDateIsBeforeFromDate;

	@Value("${working.hours}")
	private Integer workingHours;

	@Value("${employee.getManagerId.url}")
	private String employeeGetManagerIdUrl;

	@Value("${holidays.url}")
	private String holidaysUrl;

	@Value("${employee.record.notFound}")
	private String employeeRecordNotFound;

	@Value("${already.timesheet.submitted}")
	private String alreadyTimesheetSubmitted;

	@Value("${holiday.occured}")
	private String holidayOccured;

	@Override
	public ResponseDto submitTimesheet(TimesheetDto timesheetDto) {

		List<LocalDate> holidayDtos = validateSubmission(timesheetDto);
		
		Optional<Timesheet> timesheet = timesheetRepository.findByForDateAndEmployeeId(timesheetDto.getFromDate(),
				timesheetDto.getEmployeeId());
		if (timesheet.isPresent()) {
			throw new AlreadyTimesheetSubmitted(alreadyTimesheetSubmitted);
		}

		EmployeeDto employeeDto = getEmployeeManager(timesheetDto.getEmployeeId());

		if (employeeDto == null) {
			throw new RecordNotFoundException(employeeRecordNotFound);
		}

		
		List<Timesheet> timesheets = new ArrayList<>();
//		LocalDate appliedLocalDate = timesheetDto.getFromDate();
		LocalDate submittedDate = LocalDate.now();
//		int noFDays = timesheetUtility.calculateDays(timesheetDto.getFromDate(), timesheetDto.getToDate());

		for (LocalDate date = timesheetDto.getFromDate(); date.isBefore(timesheetDto.getToDate().plusDays(1)); date = date.plusDays(1)) {
			if(date.getDayOfWeek().equals(WeekEnd.SATURDAY)||date.getDayOfWeek().equals(WeekEnd.SUNDAY) || holidayDtos.contains(date)) {
				continue;
			}
			timesheets.add(Timesheet.builder().employeeId(timesheetDto.getEmployeeId()).reportingManagerId(employeeDto.getManagerId())
					.forDate(date).workingHours(timesheetDto.getWorkingHours())
					.submittedDate(submittedDate).status(Status.PENDING.value).build());
		}
		timesheetRepository.saveAll(timesheets);
		return ResponseDto.builder().status(TimesheetUtility.STATUS_SUCCCESS)
				.message(TimesheetUtility.TIMESHEET_SUCCESS_MESSAGE).build();
	}

	/**
	 * @param timesheetDto
	 * @return Integer
	 * 
	 */
	public List<LocalDate> validateSubmission(TimesheetDto timesheetDto) {
		if (timesheetDto.getFromDate().compareTo(LocalDate.now()) > 0
				|| timesheetDto.getToDate().compareTo(LocalDate.now()) > 0) {
			throw new InvalidDateException(cantSubmitForFutureDate);
		} else if (timesheetDto.getToDate().isBefore(timesheetDto.getFromDate())) {
			throw new InvalidDateException(toDateIsBeforeFromDate);
		}

		/**
		 * preparing query parameter
		 */
		StringBuilder sb = new StringBuilder("?");
		for (LocalDate date = timesheetDto.getFromDate(); date
				.isBefore(timesheetDto.getToDate().plusDays(1)); date = date.plusDays(1)) {
			sb.append(TimesheetUtility.HOLIDAY_DATE + date + TimesheetUtility.AND);
		}
		String holidayDateUrl = sb.toString();
		holidayDateUrl = holidayDateUrl.substring(0, holidayDateUrl.lastIndexOf(TimesheetUtility.AND));

		/**
		 * Hitting API to get List of holidays
		 */
		List<LocalDate> holidayDtos = restTemplate.exchange(holidaysUrl + holidayDateUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<LocalDate>>() {
				}).getBody();
return holidayDtos;
	/*	if (holidayDtos != null && !holidayDtos.isEmpty()) {
			StringBuilder holidaybuilder = new StringBuilder("?");
			holidayDtos.stream().forEach(holidayDto -> holidaybuilder.append(", " + holidayDto.getHolidayDate()));
			throw new HolidayDateException(holidayOccured + holidaybuilder.toString());
		}*/

		/*
		 * Optional<Timesheet> timesheet =
		 * timesheetRepository.findByForDateAndEmployeeId(timesheetDto.getFromDate(),
		 * timesheetDto.getEmployeeId()); if (timesheet.isPresent()) { throw new
		 * AlreadyTimesheetSubmitted(alreadyTimesheetSubmitted); }
		 * 
		 * EmployeeDto employeeDto = getEmployeeManager(timesheetDto.getEmployeeId());
		 * 
		 * if (employeeDto == null) { throw new
		 * RecordNotFoundException(employeeRecordNotFound); } return
		 * employeeDto.getManagerId();
		 */
	}

	public EmployeeDto getEmployeeManager(Integer employeeId) {

		log.info("TimesheetServiceImpl :: getEmployeeManager : employeeId -- {}", employeeId);
		EmployeeDto employeeDto = restTemplate
				.getForEntity(employeeGetManagerIdUrl + employeeId, EmployeeDto.class).getBody();
		log.info("TimesheetServiceImpl :: managerid -- {}", employeeDto.getManagerId());

		return employeeDto;
//		return EmployeeDto.builder().employeeId(101).managerId(1010).build();
	}

	@Override
	public ResponseDto approveTimesheet(ApproveDto approveDto, Integer managerId) {

		Optional<List<Timesheet>> timesheetOptional = timesheetRepository
				.findByEmployeeIdAndReportingManagerIdAndTimesheetIdIn(approveDto.getEmployeeId(), managerId,
						approveDto.getTimesheetIds());

		if (!timesheetOptional.isPresent()) {
			throw new RecordNotFoundException(employeeRecordNotFound);
		}

		LocalDate currentDate = LocalDate.now();
		if (approveDto.getStatus() == Status.APPROVED.value) {
			timesheetOptional.get().stream().forEach(timesheet -> {
				timesheet.setStatus(Status.APPROVED.value);
				timesheet.setLastModifiedDate(currentDate);
			});
		} else {
			timesheetOptional.get().stream().forEach(timesheet -> {
				timesheet.setStatus(Status.REJECTED.value);
				timesheet.setLastModifiedDate(currentDate);
				timesheet.setRemarks(approveDto.getRejectMessage());
			});
		}

		timesheetRepository.saveAll(timesheetOptional.get());

		ResponseDto responseDto = ResponseDto.builder().message(TimesheetUtility.VERIFED_SUCCESS_MESSAGE)
				.status(TimesheetUtility.STATUS_SUCCCESS).build();
		return responseDto;
	}

}
