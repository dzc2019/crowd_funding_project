package com.zcdeng.crowd.service.api;

import com.github.pagehelper.PageInfo;
import com.zcdeng.crowd.entity.Role;

import java.util.List;

public interface RoleService {
    PageInfo<Role> getPageInfo(Integer pageNum, Integer pageSize, String keyword);

    void saveRole(Role role);

    void updateRole(Role role);

    void deleteRole(Integer[] id);

    void deleteRole(Integer id);

    List<Role> getAssignedRole(Integer adminId);

    List<Role> getUnAssignedRole(Integer adminId);
}