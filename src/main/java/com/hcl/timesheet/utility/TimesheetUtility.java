package com.hcl.timesheet.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Component;

/**
 * @author Laxman
 *
 */
@Component
public class TimesheetUtility {

	public static final String EMPLOYEE_DOES_NOT_EXIST_EXCEPTION = "Invalid employee";

	public static final String INVALID_DATE_EEXCEPTION = "Invalid leave date";

	public static final String SUBMITTED_STATUS_PENDING = "PENDING";

	public static final String STATUS_SUCCCESS = "Success";

	public static final String STATUS_FAIL = "Fail";

	public static final String TIMESHEET_SUCCESS_MESSAGE = "Timesheet submitted successfully.";

	public static final String VERIFED_SUCCESS_MESSAGE = "Timesheet approved successsfully.";

	public static final String AND = "&";
	public static final String HOLIDAY_DATE = "holidayDate=";

	public LocalDate getDateInFormat(String date) {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		return LocalDate.parse(date, formatter);
	}

	public int calculateDays(String fromDate, String toDate) {
		long noOfDaysBetween = ChronoUnit.DAYS.between(getDateInFormat(fromDate), getDateInFormat(toDate));

		return (int) noOfDaysBetween + 1;
	}

	public int calculateDays(LocalDate fromDate, LocalDate toDate) {
		long noOfDaysBetween = ChronoUnit.DAYS.between(fromDate, toDate);
		return (int) noOfDaysBetween;
	}
}
