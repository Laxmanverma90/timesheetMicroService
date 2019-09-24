package com.hcl.timesheet.utility;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.hcl.timesheet.dto.EmployeeDto;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxman
 *
 */
@Slf4j
@Component
public class TimesheetUtility {

	@Autowired
	private RestTemplate restTemplate;

	public static final String EMPLOYEE_DOES_NOT_EXIST_EXCEPTION = "Invalid employee";

	public static final String INVALID_DATE_EEXCEPTION = "Invalid leave date";

	public static final String SUBMITTED_STATUS_PENDING = "PENDING";

	public static final String STATUS_SUCCCESS = "Success";

	public static final String STATUS_FAIL = "Fail";

	public static final String TIMESHEET_SUCCESS_MESSAGE = "Timesheet submitted successfully.";

	public static final String VERIFED_SUCCESS_MESSAGE = "Timesheet approved successsfully.";

	public static final String AND = "&";
	public static final String HOLIDAY_DATE = "myDays=";
	public static final String EMPTY = "";

	@Value("${holidays.url}")
	private String holidaysUrl;

	@Value("${employee.getManagerId.url}")
	private String employeeGetManagerIdUrl;

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

	public List<LocalDate> webServiceCall(String holidayDateUrl) {
		return restTemplate.exchange(holidaysUrl + holidayDateUrl, HttpMethod.GET, null,
				new ParameterizedTypeReference<List<LocalDate>>() {
				}).getBody();
	}

	/**
	 * @param Integer employeeId
	 * @return EmployeeDto
	 * @date 19 SEPT 2019
	 */
	public EmployeeDto getEmployeeManager(Integer employeeId) {

		log.info(":: getEmployeeManager : employeeId -- {}", employeeId);
		EmployeeDto employeeDto = null;
		employeeDto = restTemplate.getForEntity(employeeGetManagerIdUrl + employeeId, EmployeeDto.class).getBody();
		log.info("TimesheetServiceImpl :: managerid -- {}", employeeDto.getManagerId());
		return employeeDto;
	}
}
