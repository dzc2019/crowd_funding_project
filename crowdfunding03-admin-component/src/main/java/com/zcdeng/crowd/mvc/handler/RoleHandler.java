package com.zcdeng.crowd.mvc.handler;

import com.github.pagehelper.PageInfo;
import com.zcdeng.crowd.util.ResultEntity;
import com.zcdeng.crowd.entity.Role;
import com.zcdeng.crowd.service.api.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@PreAuthorize(value = "hasRole('部长')")
public class RoleHandler {

    @Autowired
    private RoleService roleService;

    @ResponseBody
    @RequestMapping("/role/get/page/info.json")
    public ResultEntity<PageInfo<Role>> getPageInfo(
            @RequestParam(value="pageNum", defaultValue="1") Integer pageNum,
            @RequestParam(value="pageSize", defaultValue="10") Integer pageSize,
            @RequestParam(value="keyword", defaultValue="") String keyword){
        PageInfo<Role> pageInfo = roleService.getPageInfo(pageNum, pageSize, keyword);

        return ResultEntity.successWithData(pageInfo);
    }

    @ResponseBody
    @RequestMapping("/role/save.json")
    public ResultEntity<String> saveRole(Role role) {
        try{
            roleService.saveRole(role);
        }catch (Exception e){
            return ResultEntity.failed(e.getMessage());
        }
        return ResultEntity.successWithoutData();
    }

    @ResponseBody
    @RequestMapping("/role/update.json")
    public ResultEntity<String> updateRole(Role role) {
        try{
            roleService.updateRole(role);
        }catch (Exception e){
            return ResultEntity.failed(e.getMessage());
        }
        return ResultEntity.successWithoutData();
    }

    @ResponseBody
    @RequestMapping("/role/delete.json")
    public ResultEntity<String> deleteRole(@RequestBody Integer[] ids) {
        roleService.deleteRole(ids);
        return ResultEntity.successWithoutData();
    }


}
