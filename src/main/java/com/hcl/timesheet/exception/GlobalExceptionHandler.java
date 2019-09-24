package com.hcl.timesheet.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hcl.timesheet.utility.TimesheetUtility;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxman
 *
 */
@Slf4j
@ControllerAdvice
@RestController
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	/**
	 * 
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<ErrorResponse> recordNotFoundException(CustomException ex) {

		log.info(" :: recordNotFoundException -- ");
		ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), TimesheetUtility.STATUS_FAIL);

		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(HttpStatusCodeException.class)
	public ResponseEntity<String> parentException(HttpStatusCodeException ex) {

		log.info(" :: parentException -- ");
		return ResponseEntity.status(ex.getRawStatusCode()).headers(ex.getResponseHeaders())
				.body(ex.getResponseBodyAsString());
	}
	
}
