package com.zcdeng.crowd.mvc.config;

import com.zcdeng.crowd.entity.Admin;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class SecurityAdmin extends User {
    private Admin admin;

    public SecurityAdmin(Admin admin, Collection<? extends GrantedAuthority> authorities) {
        super(admin.getLoginAcct(), admin.getUserPswd(), authorities);
        this.admin = admin;
        this.admin.setUserPswd(null);
    }

    public Admin getAdmin() {
        return admin;
    }
}
