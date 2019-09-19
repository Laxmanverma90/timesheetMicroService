package com.hcl.timesheet.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hcl.timesheet.Entity.Timesheet;

public interface TimesheetRepository extends JpaRepository<Timesheet, Integer> {

	Optional<Timesheet> findByForDateAndEmployeeId(LocalDate forDate, Integer employeeId);

	Optional<List<Timesheet>> findByEmployeeIdAndReportingManagerIdAndTimesheetIdIn(Integer employeeId,
			Integer reportingManagerId, List<Integer> timesheetIds);
}
