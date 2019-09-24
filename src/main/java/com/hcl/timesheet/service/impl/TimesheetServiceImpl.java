package com.hcl.timesheet.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hcl.timesheet.dto.ApproveDto;
import com.hcl.timesheet.dto.EmployeeDto;
import com.hcl.timesheet.dto.ResponseDto;
import com.hcl.timesheet.dto.TimesheetDto;
import com.hcl.timesheet.entity.Timesheet;
import com.hcl.timesheet.exception.CustomException;
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

	@Value("${cant.submit.for.future.date}")
	private String cantSubmitForFutureDate;

	@Value("${toDate.isBefore.fromDate}")
	private String toDateIsBeforeFromDate;

	@Value("${working.hours}")
	private Integer workingHours;

	@Value("${employee.record.notFound}")
	private String employeeRecordNotFound;

	@Value("${already.timesheet.submitted}")
	private String alreadyTimesheetSubmitted;

	@Value("${holiday.occured}")
	private String holidayOccured;

	@Override
	public ResponseDto submitTimesheet(TimesheetDto timesheetDto) {

		log.info(" :: submitTimesheet -- START ----");

		List<LocalDate> holidayDtos = validateSubmission(timesheetDto);

		Optional<Timesheet> timesheet = timesheetRepository.findByForDateAndEmployeeId(timesheetDto.getFromDate(),
				timesheetDto.getEmployeeId());
		if (timesheet.isPresent()) {
			throw new CustomException(alreadyTimesheetSubmitted);
		}

		log.info(" :: submitTimesheet -- getting managerId based on EmployeeId ----");
		EmployeeDto employeeDto = timesheetUtility.getEmployeeManager(timesheetDto.getEmployeeId());

		if (employeeDto == null) {
			throw new CustomException(employeeRecordNotFound);
		}

		List<Timesheet> timesheets = new ArrayList<>();
		LocalDate submittedDate = LocalDate.now();

		/**
		 * Looping fromDate to toDate if there is any WEEKEND/HOLIDAY will skip saving
		 * to database for the same
		 */
		log.info(" :: submitTimesheet -- LOOPing fromDate to toDate ----");
		for (LocalDate date = timesheetDto.getFromDate(); date
				.isBefore(timesheetDto.getToDate().plusDays(1)); date = date.plusDays(1)) {
			if (date.getDayOfWeek().name().equals(WeekEnd.SATURDAY.name())
					|| date.getDayOfWeek().name().equals(WeekEnd.SUNDAY.name()) || holidayDtos.contains(date)) {
				continue;
			}
			timesheets.add(Timesheet.builder().employeeId(timesheetDto.getEmployeeId())
					.reportingManagerId(employeeDto.getManagerId()).forDate(date)
					.workingHours(timesheetDto.getWorkingHours()).lastModifiedDate(submittedDate)
					.remarks(TimesheetUtility.EMPTY).submittedDate(submittedDate).status(Status.PENDING.value).build());
		}

		log.info(" :: submitTimesheet -- SAVING all record ----");
		timesheetRepository.saveAll(timesheets);
		return ResponseDto.builder().status(TimesheetUtility.STATUS_SUCCCESS)
				.message(TimesheetUtility.TIMESHEET_SUCCESS_MESSAGE).build();
	}

	/**
	 * @param timesheetDto
	 * @return Integer
	 * @date 19 SEPT 2019
	 * 
	 *       Validation fromDate & todate with eachother and current date, So that
	 *       user can't submit for future date and form date cann't be after toDate.
	 * 
	 *       Getting Holidays for between fromDate & toDate from Holiday Service
	 */
	public List<LocalDate> validateSubmission(TimesheetDto timesheetDto) {

		log.info(" :: validateSubmission -- START ----");

		if (timesheetDto.getFromDate().compareTo(LocalDate.now()) > 0
				|| timesheetDto.getToDate().compareTo(LocalDate.now()) > 0) {
			throw new CustomException(cantSubmitForFutureDate);
		} else if (timesheetDto.getToDate().isBefore(timesheetDto.getFromDate())) {
			throw new CustomException(toDateIsBeforeFromDate);
		}

		/**
		 * preparing query parameter
		 */
		log.info(" :: validateSubmission -- preparing queryParameter ----");
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
		log.info(" :: validateSubmission -- calling HolidatService ----");
		return timesheetUtility.webServiceCall(holidayDateUrl);
	}

	/**
	 * @param ApproveDto approveDto, Integer managerId
	 * @return ResponseDto
	 * @date 19 SEPT 2019
	 */
	@Override
	public ResponseDto approveTimesheet(ApproveDto approveDto, Integer managerId) {

		log.info(" :: approveTimesheet -- START ----");
		Optional<List<Timesheet>> timesheetOptional = timesheetRepository
				.findByEmployeeIdAndReportingManagerIdAndTimesheetIdIn(approveDto.getEmployeeId(), managerId,
						approveDto.getTimesheetIds());

		if (!timesheetOptional.isPresent()) {
			throw new CustomException(employeeRecordNotFound);
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

		return ResponseDto.builder().message(TimesheetUtility.VERIFED_SUCCESS_MESSAGE)
				.status(TimesheetUtility.STATUS_SUCCCESS).build();
	}

}
