package com.zcdeng.crowd.service.impl;

import com.zcdeng.crowd.entity.Auth;
import com.zcdeng.crowd.entity.AuthExample;
import com.zcdeng.crowd.mapper.AuthMapper;
import com.zcdeng.crowd.service.api.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private AuthMapper authMapper;

    @Override
    public List<Integer> getAssignedAuthIdByRoleId(Integer roleId) {
        return authMapper.selectAssignedAuthIdByRoleId(roleId);
    }

    @Override
    public void saveRoleAuthRelathinship(Map<String, List<Integer>> map) {
        // 1.获取 roleId 的值
        List<Integer> roleIdList = map.get("roleId");
        Integer roleId = roleIdList.get(0);
        // 2.删除旧关联关系数据
        authMapper.deleteOldRelationship(roleId);
// 3.获取 authIdList
        List<Integer> authIdList = map.get("authIdArray");
// 4.判断 authIdList 是否有效
        if(authIdList != null && authIdList.size() > 0) {
            authMapper.insertNewRelationship(roleId, authIdList);
        }
    }

    @Override
    public List<Auth> getAll() {
        return authMapper.selectByExample(new AuthExample());
    }
}
