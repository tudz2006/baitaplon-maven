package baitaplon;

import java.util.*;
import baitaplon.backend.*;
public class Baitaplon {
    private static String nhanVienIdHienTai = null;
    private static String nhanVienTenHienTai = null;
    public static void main(String[] args) {
        // Ensure UTF-8 output on Windows console
        try {
            System.setOut(new java.io.PrintStream(System.out, true, java.nio.charset.StandardCharsets.UTF_8));
            System.setErr(new java.io.PrintStream(System.err, true, java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception ignore) {}
        Scanner sc = new Scanner(System.in);
        helper hp = new helper();
        chucnang cn = new chucnang();
        
        while (true){
            hp.xoamh();
            System.out.println("=== HỆ THỐNG QUẢN LÝ BÁN HÀNG ===");
            System.out.println("Nhân viên hiện tại: " + (nhanVienTenHienTai == null ? "Chưa chọn" : (nhanVienTenHienTai + " (" + nhanVienIdHienTai + ")")));
            System.out.println("1. Chọn nhân viên ca hiện tại");
            System.out.println("2. Tạo đơn hàng mới");
            System.out.println("3. Hiển thị tất cả đơn hàng");
            System.out.println("4. Nhập nguyên liệu");
            System.out.println("5. Quản lý sản phẩm xuất hàng");
            System.out.println("6. In danh sách nhân viên");
            System.out.println("7. Thoát");
            System.out.print("Nhập lựa chọn: ");
            
            int choice = sc.nextInt();
            sc.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    System.out.println("\n=== CHỌN NHÂN VIÊN CA HIỆN TẠI ===");
                    chonNhanVienHienTai(hp, sc);
                    break;
                case 2:
                    System.out.println("\n=== TẠO ĐƠN HÀNG MỚI ===");
                    if (nhanVienIdHienTai == null) {
                        System.out.println("Bạn chưa chọn nhân viên ca hiện tại. Vui lòng chọn ở mục 1.");
                        break;
                    }
                    cn.taodonhangmoi(nhanVienIdHienTai, nhanVienTenHienTai);
                    break;
                case 3:
                    System.out.println("\n=== HIỂN THỊ TẤT CẢ ĐƠN HÀNG ===");
                    hienThiTatCaDonHang(hp);
                    break;
                case 4:
                    System.out.println("\n=== NHẬP NGUYÊN LIỆU ===");
                    nhapNguyenLieu(hp, sc);
                    break;
                case 5:
                    System.out.println("\n=== QUẢN LÝ SẢN PHẨM XUẤT HÀNG ===");
                    quanLyXuatHang(hp, sc);
                    break;
                case 6:
                    System.out.println("\n=== DANH SÁCH NHÂN VIÊN ===");
                    inDanhSachNhanVien(hp);
                    break;
                case 7:
                    System.out.println("Cảm ơn bạn đã sử dụng hệ thống!");
                    sc.close();
                    return;
                default:
                    System.out.println("Lựa chọn không hợp lệ! Vui lòng chọn từ 1-7.");
                    break;
            }
            
            System.out.println("\nNhấn Enter để tiếp tục...");
            sc.nextLine();
            hp.xoamh();
        }
    }
    
    private static void hienThiTatCaDonHang(helper hp) {
        try {
            List<Object[]> orders = hp.getinfo("donhang");
            if (orders.isEmpty()) {
                System.out.println("Không có đơn hàng nào!");
                return;
            }
            
            System.out.println("=== DANH SÁCH ĐƠN HÀNG ===");
            for (int i = 0; i < orders.size(); i++) {
                Object[] order = orders.get(i);
                System.out.println((i + 1) + ". Mã đơn hàng: " + order[1] + 
                                 ", Giá tiền: " + order[3] + " VND" +
                                 ", Trạng thái: " + (order[4].equals(1) ? "Đã thanh toán" : "Chưa thanh toán"));
            }
        } catch (Exception e) {
            System.out.println("Lỗi khi hiển thị đơn hàng: " + e.getMessage());
        }
    }
    
    private static void nhapNguyenLieu(helper hp, Scanner sc) {
        System.out.print("Nhập tên nguyên liệu: ");
        String name = sc.nextLine();
        
        System.out.print("Nhập giá: ");
        int price = sc.nextInt();
        sc.nextLine(); // consume newline
        
        System.out.print("Nhập số lượng: ");
        int count = sc.nextInt();
        sc.nextLine(); // consume newline
        
        boolean success = hp.nhaphang_add(name, price, count);
        if (success) {
            System.out.println("Đã thêm nguyên liệu thành công!");
        } else {
            System.out.println("Lỗi khi thêm nguyên liệu!");
        }
    }

    private static void inDanhSachNhanVien(helper hp) {
        try {
            Map<String,String> m = hp.nhanvien_list();
            if (m == null || m.isEmpty()) {
                System.out.println("Không có nhân viên!");
                return;
            }
            int i = 1;
            for (Map.Entry<String,String> e : m.entrySet()) {
                System.out.println((i++) + ". Mã: " + e.getKey() + ", Tên: " + e.getValue());
            }
        } catch (Exception ex) {
            System.out.println("Lỗi khi in danh sách nhân viên: " + ex.getMessage());
        }
    }

    private static void quanLyXuatHang(helper hp, Scanner sc) {
        while (true) {
            System.out.println("1. Danh sách sản phẩm");
            System.out.println("2. Thêm sản phẩm");
            System.out.println("3. Sửa sản phẩm");
            System.out.println("4. Xóa sản phẩm");
            System.out.println("5. Quay lại");
            System.out.print("Chọn: ");
            String line = sc.nextLine();
            int c;
            try { c = Integer.parseInt(line); } catch (Exception ex) { System.out.println("Không hợp lệ"); continue; }
            switch (c) {
                case 1:
                    danhSachXuatHang(hp);
                    break;
                case 2:
                    themXuatHang(hp, sc);
                    break;
                case 3:
                    suaXuatHang(hp, sc);
                    break;
                case 4:
                    xoaXuatHang(hp, sc);
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Không hợp lệ");
            }
        }
    }

    private static void danhSachXuatHang(helper hp) {
        List<Object[]> list = hp.getinfo("xuathang");
        if (list == null || list.isEmpty()) {
            System.out.println("Không có sản phẩm");
            return;
        }
        System.out.println("=== Danh sách xuất hàng ===");
        for (Object[] row : list) {
            System.out.println("ID: " + row[0] + ", Tên: " + row[1] + ", Giá: " + row[2] + ", Nguyên liệu: " + row[3]);
        }
    }

    private static void themXuatHang(helper hp, Scanner sc) {
        System.out.print("Tên: ");
        String name = sc.nextLine();
        System.out.print("Giá: ");
        int price;
        try { price = Integer.parseInt(sc.nextLine()); } catch (Exception e) { System.out.println("Giá không hợp lệ"); return; }
        System.out.print("Nguyên liệu: ");
        String nguyenlieu = sc.nextLine();
        boolean ok = hp.xuathang_add(name, price, nguyenlieu);
        System.out.println(ok ? "Đã thêm" : "Thêm thất bại");
    }

    private static void suaXuatHang(helper hp, Scanner sc) {
        danhSachXuatHang(hp);
        System.out.print("Nhập ID cần sửa: ");
        int id;
        try { id = Integer.parseInt(sc.nextLine()); } catch (Exception e) { System.out.println("ID không hợp lệ"); return; }
        Map<String,Object> update = new HashMap<>();
        System.out.print("Tên mới (bỏ trống nếu giữ nguyên): ");
        String name = sc.nextLine();
        if (!name.isBlank()) update.put("name", name);
        System.out.print("Giá mới (bỏ trống nếu giữ nguyên): ");
        String p = sc.nextLine();
        if (!p.isBlank()) {
            try { update.put("price", Integer.parseInt(p)); } catch (Exception e) { System.out.println("Giá không hợp lệ"); }
        }
        System.out.print("Nguyên liệu mới (bỏ trống nếu giữ nguyên): ");
        String nl = sc.nextLine();
        if (!nl.isBlank()) update.put("nguyenlieu", nl);
        if (update.isEmpty()) { System.out.println("Không có thay đổi"); return; }
        boolean ok = hp.xuathang_update(id, update);
        System.out.println(ok ? "Đã cập nhật" : "Cập nhật thất bại");
    }

    private static void xoaXuatHang(helper hp, Scanner sc) {
        danhSachXuatHang(hp);
        System.out.print("Nhập ID cần xóa: ");
        int id;
        try { id = Integer.parseInt(sc.nextLine()); } catch (Exception e) { System.out.println("ID không hợp lệ"); return; }
        boolean ok = hp.xuathang_delete(id);
        System.out.println(ok ? "Đã xóa" : "Xóa thất bại");
    }
    private static void chonNhanVienHienTai(helper hp, Scanner sc) {
        Map<String,String> m = hp.nhanvien_list();
        if (m == null || m.isEmpty()) {
            System.out.println("Không có nhân viên để chọn!");
            return;
        }
        List<Map.Entry<String,String>> list = new ArrayList<>(m.entrySet());
        for (int i = 0; i < list.size(); i++) {
            Map.Entry<String,String> e = list.get(i);
            System.out.println((i + 1) + ". Mã: " + e.getKey() + ", Tên: " + e.getValue());
        }
        System.out.print("Nhập số thứ tự nhân viên: ");
        int idx;
        try {
            idx = Integer.parseInt(sc.nextLine());
        } catch (Exception ex) {
            System.out.println("Giá trị không hợp lệ.");
            return;
        }
        if (idx < 1 || idx > list.size()) {
            System.out.println("Số thứ tự ngoài phạm vi.");
            return;
        }
        Map.Entry<String,String> chosen = list.get(idx - 1);
        nhanVienIdHienTai = chosen.getKey();
        nhanVienTenHienTai = chosen.getValue();
        System.out.println("Đã chọn: " + nhanVienTenHienTai + " (" + nhanVienIdHienTai + ")");
    }
}
