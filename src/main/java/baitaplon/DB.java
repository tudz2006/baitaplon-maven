/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package baitaplon;

import baitaplon.utils.ConfigReader;
import baitaplon.utils.HikariCPManager;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DB {

    private final String url;
    private final String user;
    private final String pass;
    private String lastError = null;
    public DB(String url,String user, String pass,String name) {
        this.url = "jdbc:mysql://"+url+"/"+name+"?useSSL=false&serverTimezone=UTC&useUnicode=true&characterEncoding=UTF-8";
        this.user = user;
        this.pass = pass;
        try { Class.forName("com.mysql.cj.jdbc.Driver"); } catch (ClassNotFoundException ignore) {}
    }
    
    public DB(){
        // Load configuration from properties file
        String host = ConfigReader.getProperty("db.host", "localhost");
        int port = ConfigReader.getIntProperty("db.port", 3306);
        String name = ConfigReader.getProperty("db.name", "baitaplonjava");
        this.user = ConfigReader.getProperty("db.user", "root");
        this.pass = ConfigReader.getProperty("db.password", "");
        
        // Build connection URL with configurable options
        boolean useSSL = ConfigReader.getBooleanProperty("db.use.ssl", false);
        String timezone = ConfigReader.getProperty("db.server.timezone", "UTC");
        boolean useUnicode = ConfigReader.getBooleanProperty("db.use.unicode", true);
        String encoding = ConfigReader.getProperty("db.character.encoding", "UTF-8");
        
        StringBuilder urlBuilder = new StringBuilder("jdbc:mysql://");
        urlBuilder.append(host).append(":").append(port).append("/").append(name);
        urlBuilder.append("?useSSL=").append(useSSL);
        urlBuilder.append("&serverTimezone=").append(timezone);
        urlBuilder.append("&useUnicode=").append(useUnicode);
        urlBuilder.append("&characterEncoding=").append(encoding);
        
        this.url = urlBuilder.toString();
        
        // Log configuration source
        System.out.println("Database configuration loaded from: " + ConfigReader.getConfigSource());
        System.out.println("Database URL: " + this.url.replace(this.pass, "***"));
        
        try { 
            Class.forName("com.mysql.cj.jdbc.Driver"); 
        } catch (ClassNotFoundException ignore) {}
    }
    
    
    public String getLastError() {
        return lastError;
    }

    private void setLastError(Exception e) {
        lastError = (e == null) ? null : (e.getClass().getSimpleName() + ": " + e.getMessage());
    }

    // =========================
    // Public API (mỗi lần tự mở/đóng kết nối)
    // =========================

    /** INSERT/UPDATE/DELETE - returns number of affected rows */
    public int execute(String sql, Object... params) throws SQLException {
        // Bỏ params, chỉ dùng sql thường
        try (Connection con = getConnection();
             java.sql.Statement st = con.createStatement()) {
            // Check if SQL is a SELECT statement
            String trimmedSql = sql.trim().toUpperCase();
            if (trimmedSql.startsWith("SELECT")) {
                throw new SQLException("Use fetchAll(), fetchArray(), or fetchScalar() for SELECT statements");
            }
            return st.executeUpdate(sql);
        }
    }

    /** SELECT -> List<Map<col,value>> (giống fetch_assoc) */
    public List<Map<String, Object>> fetchAll(String sql, Object... params) throws SQLException {
        try (Connection con = getConnection()) {
            return fetchAll(con, sql);
        }
    }

    /** SELECT -> List<Object[]> (giống fetch_array dạng chỉ số) */
    public List<Object[]> fetchArray(String sql, Object... params) throws SQLException {
        try (Connection con = getConnection()) {
            return fetchArray(con, sql);
        }
    }

    /** Lấy 1 dòng đầu tiên (Map) hoặc null */
    public Map<String, Object> fetchOne(String sql, Object... params) throws SQLException {
        List<Map<String, Object>> all = fetchAll(sql);
        return all.isEmpty() ? null : all.get(0);
    }

    /** Lấy 1 ô (hàng 1, cột 1) */
    public Object fetchScalar(String sql, Object... params) throws SQLException {
        try (Connection con = getConnection();
             java.sql.Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            return rs.next() ? rs.getObject(1) : null;
        }
    }

    /** Map ra kiểu T tùy ý */
    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
        try (Connection con = getConnection()) {
            return query(con, sql, mapper);
        }
    }
    
    


    // =========================
    // Transaction API
    // =========================
    public Transaction beginTransaction() throws SQLException {
        Connection con = getConnection();
        con.setAutoCommit(false);
        return new Transaction(con);
    }

    public final class Transaction implements AutoCloseable {
        private final Connection con;
        private boolean done = false;

        private Transaction(Connection con) {
            this.con = con;
        }

        public int execute(String sql, Object... params) throws SQLException {
            try (java.sql.Statement st = con.createStatement()) {
                return st.executeUpdate(sql);
            }
        }

        public List<Map<String, Object>> fetchAll(String sql, Object... params) throws SQLException {
            return DB.this.fetchAll(con, sql);
        }

        public List<Object[]> fetchArray(String sql, Object... params) throws SQLException {
            return DB.this.fetchArray(con, sql);
        }

        public Map<String, Object> fetchOne(String sql, Object... params) throws SQLException {
            List<Map<String, Object>> all = fetchAll(sql);
            return all.isEmpty() ? null : all.get(0);
        }

        public Object fetchScalar(String sql, Object... params) throws SQLException {
            try (java.sql.Statement st = con.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                return rs.next() ? rs.getObject(1) : null;
            }
        }

        public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) throws SQLException {
            return DB.this.query(con, sql, mapper);
        }

        public void commit() throws SQLException {
            con.commit();
            done = true;
        }

        public void rollback() throws SQLException {
            con.rollback();
            done = true;
        }

        @Override
        public void close() throws SQLException {
            try {
                if (!done) con.rollback();
            } finally {
                con.setAutoCommit(true);
                con.close();
            }
        }
    }

    // =========================
    // Internal helpers (dùng lại cho cả non-tx & tx)
    // =========================

    private Connection getConnection() throws SQLException {
        // Use HikariCP connection pool for better performance
        return HikariCPManager.getConnection();
    }

    private List<Map<String, Object>> fetchAll(Connection con, String sql) throws SQLException {
        try (java.sql.Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Map<String, Object>> out = new ArrayList<>();
            while (rs.next()) out.add(rowToMap(rs));
            return out;
        }
    }

    private List<Object[]> fetchArray(Connection con, String sql) throws SQLException {
        try (java.sql.Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<Object[]> out = new ArrayList<>();
            int cols = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Object[] row = new Object[cols];
                for (int i = 1; i <= cols; i++) row[i - 1] = rs.getObject(i);
                out.add(row);
            }
            return out;
        }
    }

    private <T> List<T> query(Connection con, String sql, RowMapper<T> mapper) throws SQLException {
        try (java.sql.Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            List<T> out = new ArrayList<>();
            while (rs.next()) out.add(mapper.map(rs));
            return out;
        }
    }

    private void bind(PreparedStatement ps, Object... params) throws SQLException {
        if (params == null) return;
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            int idx = i + 1;

            if (p == null) {
                ps.setObject(idx, null);

            } else if (p instanceof LocalDate ld) {
                ps.setDate(idx, java.sql.Date.valueOf(ld));

            } else if (p instanceof LocalDateTime ldt) {
                ps.setTimestamp(idx, java.sql.Timestamp.valueOf(ldt));

            } else if (p instanceof java.util.Date d) {
                ps.setTimestamp(idx, new java.sql.Timestamp(d.getTime()));

            } else if (p instanceof java.sql.Date d) {
                ps.setDate(idx, d);

            } else if (p instanceof java.sql.Timestamp ts) {
                ps.setTimestamp(idx, ts);

            } else {
                ps.setObject(idx, p);
            }
        }
    }

    private Map<String, Object> rowToMap(ResultSet rs) throws SQLException {
        ResultSetMetaData md = rs.getMetaData();
        int cols = md.getColumnCount();
        Map<String, Object> m = new LinkedHashMap<>(cols);
        for (int i = 1; i <= cols; i++) {
            String name = md.getColumnLabel(i);
            m.put(name, rs.getObject(i));
        }
        return m;
    }
    private String safeName(String name) {
        if (name == null || !name.matches("[A-Za-z0-9_]+")) {
            throw new IllegalArgumentException("Tên bảng/cột không hợp lệ: " + name);
        }
        return "`" + name + "`";
    }
    public boolean insert(String table, Map<String, Object> data) {
        if (data == null || data.isEmpty()) {
            return false;
        }
        try {
            List<String> columns = new ArrayList<>(data.size());
            List<String> values  = new ArrayList<>(data.size());
            for (Map.Entry<String, Object> e : data.entrySet()) {
                columns.add(safeName(e.getKey()));
                Object v = e.getValue();
                if (v == null) {
                    values.add("NULL");
                } else if (v instanceof Number) {
                    values.add(v.toString());
                } else {
                    values.add("'" + v.toString().replace("'", "''") + "'");
                }
            }

            String colJoined    = String.join(",", columns);
            String valJoined    = String.join(",", values);
            String sql = "INSERT INTO " + safeName(table) + " (" + colJoined + ") VALUES (" + valJoined + ")";

            try (Connection conn = getConnection();
                 java.sql.Statement st = conn.createStatement()) {
                return st.executeUpdate(sql) > 0;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean update(String table, Map<String, Object> data, String where) {
        setLastError(null);
        if (data == null || data.isEmpty()) {
            setLastError(new IllegalArgumentException("Map dữ liệu trống."));
            return false;
        }
        try {
            List<String> sets = new ArrayList<>(data.size());
            for (Map.Entry<String, Object> e : data.entrySet()) {
                String col = safeName(e.getKey());
                Object v = e.getValue();
                String val;
                if (v == null) {
                    val = "NULL";
                } else if (v instanceof Number) {
                    val = v.toString();
                } else {
                    val = "'" + v.toString().replace("'", "''") + "'";
                }
                sets.add(col + " = " + val);
            }
            String setClause = String.join(", ", sets);

            StringBuilder sql = new StringBuilder("UPDATE ")
                    .append(safeName(table))
                    .append(" SET ")
                    .append(setClause);

            if (where != null && !where.isBlank()) {
                sql.append(" ").append(where);
            }

            try (Connection conn = getConnection();
                 java.sql.Statement st = conn.createStatement()) {
                return st.executeUpdate(sql.toString()) > 0;
            }
        } catch (Exception ex) {
            setLastError(ex);
            return false;
        }
    }

    public Result select(String table, String where) {
        setLastError(null);
        StringBuilder sql = new StringBuilder("SELECT * FROM ").append(safeName(table));
        if (where != null && !where.isBlank()) {
            sql.append(" ").append(where);
        }

        try (Connection conn = getConnection();
             java.sql.Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql.toString())) {

            return new Result(rs);

        } catch (Exception ex) {
            setLastError(ex);
            return new Result(); // empty result
        }
    }

    public boolean delete(String table, String where) {
        setLastError(null);
        if (table == null || table.isBlank()) {
            setLastError(new IllegalArgumentException("Tên bảng không hợp lệ."));
            return false;
        }
        try {
            StringBuilder sql = new StringBuilder("DELETE FROM ")
                    .append(safeName(table));
            if (where != null && !where.isBlank()) {
                sql.append(" ").append(where);
            }
            try (Connection conn = getConnection();
                 java.sql.Statement st = conn.createStatement()) {
                return st.executeUpdate(sql.toString()) > 0;
            }
        } catch (Exception ex) {
            setLastError(ex);
            return false;
        }
    }

    public static class Result {
        private final List<Map<String, Object>> rows;
        private final List<Object[]> arrayRows;
        private int cursor = -1;

        public Result() {
            this.rows = new ArrayList<>();
            this.arrayRows = new ArrayList<>();
        }

        public Result(ResultSet rs) throws SQLException {
            this.rows = new ArrayList<>();
            this.arrayRows = new ArrayList<>();
            ResultSetMetaData meta = rs.getMetaData();
            int colCount = meta.getColumnCount();
            while (rs.next()) {
                // Map row
                Map<String, Object> row = new LinkedHashMap<>();
                Object[] arr = new Object[colCount];
                for (int i = 1; i <= colCount; i++) {
                    Object val = rs.getObject(i);
                    row.put(meta.getColumnLabel(i), val);
                    arr[i - 1] = val;
                }
                rows.add(row);
                arrayRows.add(arr);
            }
        }

        /** Di chuyển tới dòng tiếp theo, trả về true nếu còn dòng */
        public boolean next() {
            if (cursor + 1 < rows.size()) {
                cursor++;
                return true;
            }
            return false;
        }

        /** Lấy dòng hiện tại (Map) */
        public Map<String, Object> getRow() {
            if (cursor >= 0 && cursor < rows.size()) {
                return rows.get(cursor);
            }
            return null;
        }

        /** Lấy dòng hiện tại dạng array */
        public Object[] getArrayRow() {
            if (cursor >= 0 && cursor < arrayRows.size()) {
                return arrayRows.get(cursor);
            }
            return null;
        }

        /** Lấy tất cả các dòng dạng Map */
        public List<Map<String, Object>> getRows() {
            return rows;
        }

        /** Lấy tất cả các dòng dạng array */
        public List<Object[]> fetchArrayAll() {
            return arrayRows;
        }

        /** Lấy dòng tiếp theo dạng array, trả về null nếu hết */
        public Object[] fetchArrayNext() {
            if (next()) {
                return getArrayRow();
            }
            return null;
        }

        /** Số dòng */
        public int rowCount() {
            return rows.size();
        }

        /** Reset con trỏ về đầu */
        public void reset() {
            cursor = -1;
        }
    }

    @FunctionalInterface
    public interface RowMapper<T> {
        T map(ResultSet rs) throws SQLException;
    }
}