package com.codemyth.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.codemyth.dto.EmployeePatchRequest;
import com.codemyth.dto.EmployeeRequest;
import com.codemyth.dto.EmployeeResponse;
import com.codemyth.model.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

	@Mapping(target = "empId", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	Employee toEntity(EmployeeRequest request);

	EmployeeResponse toResponse(Employee employee);

	@Mapping(target = "empId", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	void updateEntity(EmployeeRequest request, @MappingTarget Employee employee);

	@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
	@Mapping(target = "empId", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	void patchEntity(EmployeePatchRequest request, @MappingTarget Employee employee);
}
