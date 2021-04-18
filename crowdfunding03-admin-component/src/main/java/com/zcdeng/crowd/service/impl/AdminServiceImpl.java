package com.zcdeng.crowd.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.entity.Admin;
import com.zcdeng.crowd.entity.AdminExample;
import com.zcdeng.crowd.exception.LoginAcctAlreadyInUseException;
import com.zcdeng.crowd.exception.LoginAcctAlreadyInUseForUpdateException;
import com.zcdeng.crowd.exception.LoginFailedException;
import com.zcdeng.crowd.mapper.AdminMapper;
import com.zcdeng.crowd.service.api.AdminService;
import com.zcdeng.crowd.util.CrowdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminMapper adminMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveAdmin(Admin admin) {
        // 生成当前系统时间
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String createTime = format.format(date);
        admin.setCreateTime(createTime);
// 针对登录密码进行加密
        String source = admin.getUserPswd();
        String encoded = passwordEncoder.encode(source);
        admin.setUserPswd(encoded);
// 执行保存， 如果账户被占用会抛出异常
        try {
            adminMapper.insert(admin);
        } catch (DuplicateKeyException e) {

            e.printStackTrace();
            throw new LoginAcctAlreadyInUseException(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
        }
    }

    public List<Admin> getAll() {
        List<Admin> admins = adminMapper.selectByExample(new AdminExample());
        return admins;
    }

    public Admin getAdminByLoginAcct(String loginAcct, String userPswd) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andLoginAcctEqualTo(loginAcct);
        List<Admin> admins = adminMapper.selectByExample(adminExample);
        if (admins == null || admins.size() == 0) {
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        }
        if (admins.size() > 1) {
            throw new RuntimeException(CrowdConstant.MESSAGE_SYSTEM_ERROR_LOGIN_NOT_UNIQUE);
        }

        Admin admin = admins.get(0);

        if (admin == null) {
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        }

        String inputPswd = CrowdUtil.md5(userPswd);
        String realPswd = admin.getUserPswd();

        if (!Objects.equals(inputPswd, realPswd)) {
            throw new LoginFailedException(CrowdConstant.MESSAGE_LOGIN_FAILED);
        }

        return admin;
    }

    public PageInfo<Admin> getAdminPage(String keyword, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        List<Admin> admins = adminMapper.selectAdminListByKeyword(keyword);

        // ※辅助代码： 打印 adminList 的全类名
        Logger logger = LoggerFactory.getLogger(AdminServiceImpl.class);
        logger.debug("adminList 的全类名是： " + admins.getClass().getName());

        return new PageInfo<>(admins);
    }

    @Override
    public Admin getAdminById(Integer adminId) {
        return adminMapper.selectByPrimaryKey(adminId);
    }

    @Override
    public void updateAdmin(Admin admin) {
        try {
            adminMapper.updateByPrimaryKeySelective(admin);
        }catch (DuplicateKeyException e){
            throw new LoginAcctAlreadyInUseForUpdateException(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
        }
    }

    @Override
    public void remove(Integer adminId) {
        adminMapper.deleteByPrimaryKey(adminId);
    }

    @Override
    public void saveAdminRoleRelationship(Integer adminId, List<Integer> roleIdList) {
        // 为了简化操作：先根据 adminId 删除旧的数据，再根据 roleIdList 保存全部新的数据
        // 1.根据 adminId 删除旧的关联关系数据
        adminMapper.deleteOLdRelationship(adminId);
        // 2.根据 roleIdList 和 adminId 保存新的关联关系
        if (roleIdList != null && roleIdList.size() > 0) {
            adminMapper.insertNewRelationship(adminId, roleIdList);
        }
    }

    @Override
    public Admin getAdminByLoginAcct2(String username) {
        AdminExample adminExample = new AdminExample();
        AdminExample.Criteria criteria = adminExample.createCriteria();
        criteria.andLoginAcctEqualTo(username);
        List<Admin> list = adminMapper.selectByExample(adminExample);
        Admin admin = list.get(0);
        return admin;
    }
}
