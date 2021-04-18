package com.zcdeng.crowd.mvc.handler;

import com.github.pagehelper.PageInfo;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.entity.Admin;
import com.zcdeng.crowd.service.api.AdminService;
import com.zcdeng.crowd.util.CrowdUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class AdminHandler {

    @Autowired
    private AdminService adminService;

    private  Logger logger = LoggerFactory.getLogger(AdminHandler.class);
    @RequestMapping(value = {"/","/admin/to/login/page.html"})
    public String loginPage(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth instanceof AnonymousAuthenticationToken){
            return "admin-login";
        }
        return "admin-main";
    }

    @RequestMapping("admin/remove/{adminId}/{pageNum}/{keyword}.html")
    public String removeAdmin(@PathVariable(value = "adminId") Integer adminId,
                              @PathVariable(value = "pageNum") Integer pageNum,
                              @PathVariable(value = "keyword") String keyword) {
//        modelMap.addAttribute("admin",admin);
        adminService.remove(adminId);
        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }

    @RequestMapping("admin/do/edit/page.html")
    public String updateAdmin(Admin admin,
                              @RequestParam("pageNum") Integer pageNum,
                              @RequestParam("keyword") String keyword,
                              HttpServletRequest request) {
//        modelMap.addAttribute("admin",admin);
        request.setAttribute("admin",admin);
        logger.warn(request.getParameter("id"));
        adminService.updateAdmin(admin);
        return "redirect:/admin/get/page.html?pageNum=" + pageNum + "&keyword=" + keyword;
    }

    @RequestMapping("/admin/to/edit/page.html")
    public String toEditPage(@RequestParam("adminId") Integer adminId, ModelMap modelMap) {
        Admin admin = adminService.getAdminById(adminId);
        modelMap.addAttribute("admin", admin);
        return "admin-edit";
    }

    @PreAuthorize(value = "hasAuthority('user:save')")
    @RequestMapping("/save/admin.html")
    public String saveAdmin(Admin admin) {
        adminService.saveAdmin(admin);
        return "redirect:/admin/get/page.html?pageNum=" + Integer.MAX_VALUE;
    }

    @RequestMapping("/admin/get/page.html")
    public String getAdminPage(@RequestParam(value = "keyword", defaultValue = "") String keyword,
                               @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                               @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                               ModelMap modelMap) {
        PageInfo<Admin> adminPage = adminService.getAdminPage(keyword, pageNum, pageSize);
        modelMap.addAttribute(CrowdConstant.ATTR_NAME_PAGE_INFO, adminPage);

        return "admin-page";
    }

    @RequestMapping("/admin/do/login.html")
    public String doLogin(@RequestParam(value = "loginAcct") String loginAcct,
                          @RequestParam(value = "userPswd") String userPswd,
                          HttpSession session) {
        Admin admin = adminService.getAdminByLoginAcct(loginAcct, userPswd);
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_ADMIN, admin);
        return "redirect:/admin/to/main/page.html";
    }

    @RequestMapping("/admin/do/logout.html")
    public String doLogout(HttpSession session) {
        session.invalidate();

        return "redirect:/admin/to/login/page.html";
    }
}
