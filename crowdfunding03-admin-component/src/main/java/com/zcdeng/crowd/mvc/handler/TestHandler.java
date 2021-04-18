package com.zcdeng.crowd.mvc.handler;

import com.zcdeng.crowd.entity.Admin;
import com.zcdeng.crowd.service.api.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
public class TestHandler {
    @Autowired
    private AdminService adminService;

    @RequestMapping("/test/ssm")
    public String test(Map<String,Object> map, HttpServletRequest request){
        List<Admin> all = adminService.getAll();
        System.out.println(request.getContextPath());
        System.out.println(request.getRealPath("index.jsp"));
        System.out.println(request.getSession().getServletContext());
        map.put("req", request.getSession().getServletContext());
        int s = 10 / 0;
        map.put("admins",all);
        return "target";
    }

    @RequestMapping("/test/testAjax1")
    @ResponseBody
    public  String testAjax1(@RequestBody Integer[] arrays){
        for (int i: arrays
             ) {
            System.out.println(i);
        }
//        System.out.println(arrays.array);
        return "success";
    }
}
