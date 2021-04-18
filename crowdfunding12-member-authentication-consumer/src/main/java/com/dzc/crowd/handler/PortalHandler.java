package com.dzc.crowd.handler;

import com.dzc.crowd.api.MySQLRemoteService;
import com.dzc.crowd.entity.vo.PortalTypeVO;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
public class PortalHandler {
    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/")
    public String showPortalPage(Model model) {
        // 1.调用MySQLRemoteService提供的方法查询首页要显示的数据
        //优化
        // 先查redis
        //结果为空 加锁
        // 查数据库
        ResultEntity<List<PortalTypeVO>> resultEntity = mySQLRemoteService.getPortalTypeProjectDataRemote();
        // 2.检查查询结果
        String result = resultEntity.getOperationResult();
        if (ResultEntity.SUCCESS.equals(result)) {
            // 3.获取查询结果的数据
            List<PortalTypeVO> list = resultEntity.getQueryData();
            // 4,存入模型
            model.addAttribute(CrowdConstant.ATTR_NAME_PORTAL_DATA, list);
        }
        // 这里实际开发中需要加载数据……
        return "portal";
    }
}
