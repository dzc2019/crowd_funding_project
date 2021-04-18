package com.zcdeng.crowd.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcdeng.crowd.entity.Role;
import com.zcdeng.crowd.mapper.RoleMapper;
import com.zcdeng.crowd.service.api.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    RoleMapper roleMapper;

    @Override
    public PageInfo<Role> getPageInfo(Integer pageNum, Integer pageSize, String keyword) {
// 1.开启分页功能
        PageHelper.startPage(pageNum, pageSize);
// 2.执行查询
        List<Role> roleList = roleMapper.selectRoleByKeyword(keyword);
// 3.封装为 PageInfo 对象返回
        return new PageInfo<>(roleList);
    }

    @Override
    public void saveRole(Role role) {
        roleMapper.insert(role);
    }

    @Override
    public void updateRole(Role role) {
        roleMapper.updateByPrimaryKey(role);
    }

    @Override
    public void deleteRole(Integer[] ids){
        for(Integer id: ids){
            deleteRole(id);
        }
    }

    @Override
    public void deleteRole(Integer id) {
        roleMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<Role> getAssignedRole(Integer adminId) {
        return roleMapper.selectAssignedRole(adminId);
    }

    @Override
    public List<Role> getUnAssignedRole(Integer adminId) {
        return roleMapper.selectUnAssignedRole(adminId);
    }
}
