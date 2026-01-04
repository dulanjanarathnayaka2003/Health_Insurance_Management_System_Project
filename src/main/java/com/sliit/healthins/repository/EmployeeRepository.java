package com.sliit.healthins.repository;

import com.sliit.healthins.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    List<Employee> findByDepartment(String department);
    Optional<Employee> findByUser_Id(Long userId);  // Use User_Id for the relationship
}