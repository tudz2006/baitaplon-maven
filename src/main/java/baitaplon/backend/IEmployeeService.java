package baitaplon.backend;

import java.util.List;
import java.util.Map;

/**
 * Interface for Employee Management Service
 * Defines contract for employee CRUD operations
 */
public interface IEmployeeService {
    
    /**
     * Get all employees as a map (id -> name)
     * @return Map of employee ID to name
     */
    Map<String, String> getEmployeeList();
    
    /**
     * Get employee by ID
     * @param id Employee ID
     * @return Employee data as Map, null if not found
     */
    Map<String, Object> getEmployeeById(int id);
    
    /**
     * Get employee by name
     * @param name Employee name
     * @return Employee data as Map, null if not found
     */
    Map<String, Object> getEmployeeByName(String name);
    
    /**
     * Add new employee
     * @param name Employee name
     * @param position Employee position
     * @param phone Employee phone
     * @return true if successful, false otherwise
     */
    boolean addEmployee(String name, String position, String phone);
    
    /**
     * Update employee by ID
     * @param id Employee ID
     * @param name Employee name
     * @param position Employee position
     * @param phone Employee phone
     * @return true if successful, false otherwise
     */
    boolean updateEmployee(int id, String name, String position, String phone);
    
    /**
     * Delete employee by ID
     * @param id Employee ID
     * @return true if successful, false otherwise
     */
    boolean deleteEmployee(int id);
    
    /**
     * Get all employees
     * @return List of all employees as Object arrays
     */
    List<Object[]> getAllEmployees();
}

