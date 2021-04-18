package com.dzc.crowd.handler;

import com.dzc.crowd.api.MySQLRemoteService;
import com.dzc.crowd.entity.vo.AddressVO;
import com.dzc.crowd.entity.vo.MemberLoginVO;
import com.dzc.crowd.entity.vo.OrderProjectVO;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.util.ResultEntity;
import org.apache.tomcat.jni.Address;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class OrderConsumerHandler {
    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @RequestMapping("/confirm/return/info/{projectId}/{returnId}")
    public String showReturnConfirmInfo(
            @PathVariable("projectId") Integer projectId,
            @PathVariable("returnId") Integer returnId,
            Model model,
            HttpSession session) {
        try{
            ResultEntity<OrderProjectVO> orderProjectVOResultEntity = mySQLRemoteService.getOrderProjectVORemote(projectId, returnId);
            if(ResultEntity.SUCCESS.equals(orderProjectVOResultEntity.getOperationResult())){
                OrderProjectVO queryData = orderProjectVOResultEntity.getQueryData();
                session.setAttribute("orderProjectVO", queryData);
            }
        }catch (Exception e){
            e.printStackTrace();
            model.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, e.getMessage());
        }

        return "confirm-return";
    }

    @RequestMapping("/confirm/order/{returnCount}")
    public String showConfirmOrderInfo(@PathVariable("returnCount") Integer returnCount,
                                       HttpSession session){
        // 1.把接收到的回报数量合并到 Session 域
        OrderProjectVO orderProjectVO = (OrderProjectVO) session.getAttribute("orderProjectVO");
        orderProjectVO.setReturnCount(returnCount);
        session.setAttribute("orderProjectVO", orderProjectVO);
// 2.获取当前已登录用户的 id
        MemberLoginVO memberLoginVO = (MemberLoginVO)
                session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
        Integer memberId = memberLoginVO.getId();
// 3.查询目前的收货地址数据
        ResultEntity<List<AddressVO>> resultEntity =
                mySQLRemoteService.getAddressVORemote(memberId);
        if(ResultEntity.SUCCESS.equals(resultEntity.getOperationResult())) {
            List<AddressVO> list = resultEntity.getQueryData();
            session.setAttribute("addressVOList", list);
        }
        return "confirm-order";
    }

    @RequestMapping("/save/address")
    public String saveAddressVO(AddressVO addressVO, HttpSession session){
        ResultEntity<String> stringResultEntity = mySQLRemoteService.saveAddress(addressVO);
        OrderProjectVO orderProjectVO = (OrderProjectVO) session.getAttribute("orderProjectVO");
        Integer returnCount = orderProjectVO.getReturnCount();
        return "redirect:http://localhost:8081/order/confirm/order/"+returnCount;
    }

}
