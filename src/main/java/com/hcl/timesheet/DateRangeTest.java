package com.hcl.timesheet;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

public class DateRangeTest {
	public static void main(String[] args) {

		List<Emp> strs = new ArrayList<>();
		strs.add(Emp.builder().name("Abhi").address("Delhi").build());
		strs.add(Emp.builder().name("Ravi").address("Delhi").build());
		strs.add(Emp.builder().name("Puts").address("Lucknow").build());
		
		strs.stream().forEach(emp -> {
			emp.setName(emp.getName()+"-1");
			emp.setAddress(emp.getAddress()+"-2");
		});
		
		System.out.println(strs);
	}
}

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
class Emp{
	private String name;
	private String address;
}