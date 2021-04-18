package com.zcdeng.crowd.service.api;

import com.github.pagehelper.PageInfo;
import com.zcdeng.crowd.entity.Admin;

import java.util.List;

public interface AdminService {

     void saveAdmin(Admin admin);

    List<Admin> getAll();

    Admin getAdminByLoginAcct(String loginAcct, String userPswd);

    PageInfo<Admin> getAdminPage(String keyword, Integer pageNum, Integer pageSize);

    Admin getAdminById(Integer adminId);

    void updateAdmin(Admin admin);

    void remove(Integer adminId);

    void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList);

    Admin getAdminByLoginAcct2(String username);
}
