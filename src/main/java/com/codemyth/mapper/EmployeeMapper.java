package com.codemyth.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.model.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

	@Mapping(target = "empId", ignore = true)
	Employee toEntity(EmployeeRequest request);

	EmployeeResponse toResponse(Employee employee);

	@Mapping(target = "empId", ignore = true)
	void updateEntity(EmployeeRequest request, @MappingTarget Employee employee);
}
