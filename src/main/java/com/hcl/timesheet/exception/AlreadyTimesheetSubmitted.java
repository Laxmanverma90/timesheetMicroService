package com.hcl.timesheet.exception;

/**
 * @author Laxman
 *
 */
public class AlreadyTimesheetSubmitted extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AlreadyTimesheetSubmitted(String message) {
		super(message);
	}

}
