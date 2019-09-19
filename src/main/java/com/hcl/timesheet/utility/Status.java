package com.hcl.timesheet.utility;

/**
 * @author Laxman
 *
 */
public enum Status {

	PENDING(0), APPROVED(1), REJECTED(2);

	public final int value;

	private Status(int value) {
		this.value = value;
	}
}
