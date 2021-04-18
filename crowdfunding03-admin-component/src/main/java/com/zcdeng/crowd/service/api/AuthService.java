package com.zcdeng.crowd.service.api;

import com.zcdeng.crowd.entity.Auth;

import java.util.List;
import java.util.Map;

public interface AuthService {
    List<Integer> getAssignedAuthIdByRoleId(Integer roleId);

    void saveRoleAuthRelathinship(Map<String,List<Integer>> map);

    List<Auth> getAll();
}
