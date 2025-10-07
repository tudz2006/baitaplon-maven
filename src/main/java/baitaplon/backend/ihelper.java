/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package baitaplon.backend;

/**
 *
 * @author HI
 */
import java.util.Map;
import java.util.List;
public interface ihelper {
    public Map<String,String> nhanvien_list();
    public int create_order(Map<String,Object> map_noidung,int giatien,int trangthai,String ghichu,int nhanvien_id);
    public boolean update_order(String madonhang,Map<String,Object> update);
    public Object getvaluebyorder(String madonhang,String column);
    public Map<String,Object> get_order(String madonhang);
    public List<Object[]> getinfo(String table);
    public boolean nhaphang_add(String name ,int price,int count);
    public boolean nhaphang_update(int id,Map<String,Object> update);
    public boolean nhaphang_delete(int id);
    public boolean xuathang_add(String name ,int price,String nguyenlieu);
    public boolean xuathang_update(int id,Map<String,Object> update);
    public boolean xuathang_delete(int id);
    
}
