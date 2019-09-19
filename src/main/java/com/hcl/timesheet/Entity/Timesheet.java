package com.hcl.timesheet.Entity;

import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Laxman
 *
 */
@Setter
@Getter
@Builder
@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
public class Timesheet {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer timesheetId;
	private Integer employeeId;
	private Integer reportingManagerId;
	private LocalDate forDate;
	private Integer workingHours;
	private LocalDate submittedDate;
	private Integer status;
	private String remarks;
	private LocalDate lastModifiedDate;
}
