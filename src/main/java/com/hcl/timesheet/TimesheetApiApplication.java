package com.hcl.timesheet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * @author Laxman
 * @date 19 SEPT 2019
 * 
 *       This is the entry point of Application is used to bootstrap and launch
 *       a Spring application from a Java main method. This class automatically
 *       creates the ApplicationContext from the classpath, scan the
 *       configuration classes and launch the application. This class is very
 *       helpful in launching Spring MVC or Spring REST application using Spring
 *       Boot.
 */
@EnableDiscoveryClient
@SpringBootApplication
public class TimesheetApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(TimesheetApiApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}
}
