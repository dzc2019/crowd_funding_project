package com.zcdeng.crowd.mvc.config;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import com.zcdeng.crowd.entity.Admin;
import com.zcdeng.crowd.entity.AdminExample;
import com.zcdeng.crowd.entity.Role;
import com.zcdeng.crowd.mapper.AdminMapper;
import com.zcdeng.crowd.mapper.AuthMapper;
import com.zcdeng.crowd.mapper.RoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CrowdUserDetailService implements UserDetailsService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthMapper authMapper;

    @Override
    public UserDetails loadUserByUsername(String logAcct) throws UsernameNotFoundException {
        AdminExample adminExample = new AdminExample();
        adminExample.createCriteria().andLoginAcctEqualTo(logAcct);
        List<Admin> admins = adminMapper.selectByExample(adminExample);
        if(admins == null || admins.isEmpty()){
            throw new UsernameNotFoundException(logAcct);
        }
        Admin admin = admins.get(0);
        List<Role> roles = roleMapper.selectAssignedRole(admin.getId());
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (Role role: roles) {
            String roleName = "ROLE_" + role.getName();
            grantedAuthorities.add(new SimpleGrantedAuthority(roleName));
        }
        List<String> auths = authMapper.selectAssignedAuthByAdminId(admin.getId());
        for (String auth: auths
             ) {
            grantedAuthorities.add(new SimpleGrantedAuthority(auth));
        }

        return new SecurityAdmin(admin, grantedAuthorities);
    }
}
