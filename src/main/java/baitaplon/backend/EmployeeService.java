package baitaplon.backend;

import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Service for employee management (nhanvien table)
 * Handles basic CRUD operations for employees
 */
public class EmployeeService extends BaseService implements IEmployeeService {
    
    /**
     * Get all employees as a map (id -> name)
     */
    public Map<String, String> getEmployeeList() {
        try {
            List<Object[]> employees = getAll("nhanvien");
            Map<String, String> employeeMap = new HashMap<>();
            
            for (Object[] row : employees) {
                if (row != null && row.length >= 2 && row[0] != null) {
                    String id = String.valueOf(row[0]);
                    String name = row[1] == null ? "" : String.valueOf(row[1]);
                    employeeMap.put(id, name);
                }
            }
            return employeeMap;
        } catch (Exception e) {
            System.err.println("Error getting employee list: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Get employee by ID
     */
    public Map<String, Object> getEmployeeById(int id) {
        return getSingle("nhanvien", "id=" + id);
    }
    
    /**
     * Get employee by name
     */
    public Map<String, Object> getEmployeeByName(String name) {
        return getSingle("nhanvien", "name='" + name + "'");
    }
    
    /**
     * Add new employee
     */
    public boolean addEmployee(String name, String position, String phone) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("position", position);
        data.put("phone", phone);
        return insert("nhanvien", data);
    }
    
    /**
     * Update employee by ID
     */
    public boolean updateEmployee(int id, String name, String position, String phone) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", name);
        data.put("position", position);
        data.put("phone", phone);
        return update("nhanvien", data, "id=" + id);
    }
    
    /**
     * Delete employee by ID
     */
    public boolean deleteEmployee(int id) {
        return delete("nhanvien", "id=" + id);
    }
    
    /**
     * Get all employees
     */
    public List<Object[]> getAllEmployees() {
        return getAll("nhanvien");
    }
}
