package baitaplon.utils;

import baitaplon.backend.helper;
import baitaplon.backend.ihelper;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Quản lý phiên làm việc của nhân viên
 */
public class SessionManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionManager.class);
    private static SessionManager instance;
    private ihelper helper = new helper();
    
    private Integer currentEmployeeId;
    private String currentEmployeeName;
    private boolean isSessionActive;
    
    private SessionManager() {
        isSessionActive = false;
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }
    
    /**
     * Bắt đầu phiên làm việc với nhân viên được chọn
     */
    public boolean startSession(int employeeId) {
        try {
            List<Object[]> employees = helper.getinfo("nhanvien");
            for (Object[] emp : employees) {
                if (emp.length >= 2 && ((Integer) emp[0]).equals(employeeId)) {
                    currentEmployeeId = employeeId;
                    currentEmployeeName = (String) emp[1]; // Tên nhân viên
                    isSessionActive = true;
                    logger.info("Started session for employee: {} (ID: {})", currentEmployeeName, currentEmployeeId);
                    return true;
                }
            }
            logger.error("Employee with ID {} not found", employeeId);
            return false;
        } catch (Exception e) {
            logger.error("Error starting session", e);
            return false;
        }
    }
    
    /**
     * Kết thúc phiên làm việc hiện tại
     */
    public void endSession() {
        if (isSessionActive) {
            logger.info("Ended session for employee: {} (ID: {})", currentEmployeeName, currentEmployeeId);
        }
        currentEmployeeId = null;
        currentEmployeeName = null;
        isSessionActive = false;
    }
    
    /**
     * Kiểm tra xem có phiên làm việc đang hoạt động không
     */
    public boolean isSessionActive() {
        return isSessionActive;
    }
    
    /**
     * Lấy ID nhân viên hiện tại
     */
    public Integer getCurrentEmployeeId() {
        return currentEmployeeId;
    }
    
    /**
     * Lấy tên nhân viên hiện tại
     */
    public String getCurrentEmployeeName() {
        return currentEmployeeName;
    }
    
    /**
     * Lấy danh sách tất cả nhân viên
     */
    public List<Object[]> getAllEmployees() {
        try {
            return helper.getinfo("nhanvien");
        } catch (Exception e) {
            logger.error("Error getting employees", e);
            return null;
        }
    }
}
