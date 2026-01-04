package com.sliit.healthins;

import com.sliit.healthins.dto.EmployeeDTO;
import com.sliit.healthins.model.Employee;
import com.sliit.healthins.repository.EmployeeRepository;
import com.sliit.healthins.repository.PayrollRepository;
import com.sliit.healthins.repository.PerformanceReviewRepository;
import com.sliit.healthins.repository.UserRepository;
import com.sliit.healthins.service.HrService;
import com.sliit.healthins.util.PdfGeneratorUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HrServiceTest {

    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PayrollRepository payrollRepository;
    @Mock
    private PerformanceReviewRepository performanceReviewRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private PdfGeneratorUtil pdfGeneratorUtil;

    @InjectMocks
    private HrService hrService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddEmployee_Success() {
        Employee employee = new Employee();
        employee.setUserId(1L);
        employee.setDepartment("HR");

        when(employeeRepository.save(employee)).thenReturn(employee);

        Employee addedEmployee = hrService.addEmployee(employee);

        assertNotNull(addedEmployee);
        assertEquals("HR", addedEmployee.getDepartment());
        verify(employeeRepository, times(1)).save(employee);
    }

    @Test
    public void testDeleteEmployee_Success() {
        doNothing().when(employeeRepository).deleteById(1L);

        hrService.deleteEmployee(1L);

        verify(employeeRepository, times(1)).deleteById(1L);
    }

    @Test
    public void testGetEmployeeById_Success() {
        Employee employee = new Employee();
        employee.setId(1L);
        employee.setDepartment("HR");

        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));

        Optional<Employee> foundEmployee = hrService.getEmployeeById(1L);

        assertTrue(foundEmployee.isPresent());
        assertEquals("HR", foundEmployee.get().getDepartment());
        verify(employeeRepository, times(1)).findById(1L);
    }

    @Test
    public void testCreateEmployee_Success() {
        EmployeeDTO dto = new EmployeeDTO();
        dto.setUserId(1L);
        dto.setDepartment("HR");
        dto.setSalary(50000.0);
        dto.setPerformanceRating(4);
        
        Employee employee = new Employee();
        employee.setUserId(1L);
        employee.setDepartment("HR");
        employee.setSalary(50000.0);
        employee.setPerformanceRating(4);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        Employee createdEmployee = hrService.createEmployee(dto);

        assertNotNull(createdEmployee);
        assertEquals("HR", createdEmployee.getDepartment());
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    @Test
    public void testAddEmployee_InvalidRating() {
        Employee employee = new Employee();
        employee.setPerformanceRating(6);
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);
        
        Employee result = hrService.addEmployee(employee);
        assertNotNull(result);
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }
}