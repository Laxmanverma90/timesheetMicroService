package com.hcl.timesheet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hcl.timesheet.dto.ApproveDto;
import com.hcl.timesheet.dto.ResponseDto;
import com.hcl.timesheet.dto.TimesheetDto;
import com.hcl.timesheet.service.TimesheetService;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Laxman
 *
 */
@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin(allowedHeaders = { "*", "/" }, origins = { "*", "/" })
public class TimesheetController {

	@Autowired
	private TimesheetService timesheetService;
	
	@PostMapping("/timesheets")
	public ResponseEntity<ResponseDto> submitTimesheet(@RequestBody TimesheetDto timesheetDto){
		
		log.info("TimesheetController :: submitTimesheet --- ");
		return new ResponseEntity<>(timesheetService.submitTimesheet(timesheetDto), HttpStatus.CREATED);
	}
	
	@PutMapping("/timesheets/{managerId}")
	public ResponseEntity<ResponseDto> approveTimesheet(@RequestBody ApproveDto approveDto, @PathVariable Integer managerId){

		log.info("TimesheetController :: approveTimesheet --- ");
		return new ResponseEntity<>(timesheetService.approveTimesheet(approveDto, managerId), HttpStatus.OK);
	}
}
