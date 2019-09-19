package com.hcl.timesheet;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class TestClass {

	public static void main(String[] args) {
		/*
		 * System.out.println(Status.PENDING.value); int value =Status.PENDING.value;
		 * System.out.println("value : "+value);
		 */
		
		LocalDate currentDate = LocalDate.now();
		LocalDate localDate = LocalDate.now().plusDays(10);
		for(LocalDate date =currentDate; date.isBefore(localDate); date=date.plusDays(1)) {
			System.out.println( " ---> "+date.getDayOfWeek());
		}
		
	}
}


enum Status {

	PENDING(0), APPROVED(1), REJECTED(2);
	
	public final int value;
	 
    private Status(int value) {
        this.value = value;
    }
}