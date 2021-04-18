package com.dzc.crowd.handler;

import com.dzc.crowd.entity.po.AddressPO;
import com.dzc.crowd.entity.po.OrderPO;
import com.dzc.crowd.entity.vo.AddressVO;
import com.dzc.crowd.entity.vo.OrderProjectVO;
import com.dzc.crowd.entity.vo.OrderVO;
import com.dzc.crowd.service.api.OrderService;
import com.zcdeng.crowd.util.ResultEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class OrderProviderHandler {
    @Autowired
    private OrderService orderService;

    @RequestMapping("/get/order/project/vo/remote")
    public ResultEntity<OrderProjectVO> getOrderProjectVORemote(@RequestParam("projectId") Integer projectId,
                                                                @RequestParam("returnId") Integer returnId) {
        try {
            OrderProjectVO orderProjectVO = orderService.getOrderProjectVO(projectId, returnId);
            return ResultEntity.successWithData(orderProjectVO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/get/address/vo/remote")
    ResultEntity<List<AddressVO>> getAddressVORemote(@RequestParam("memberId") Integer memberId){
        try{
            List<AddressPO> addressPOList = orderService.getAddressPOList(memberId);
            List<AddressVO> addressVOList = new ArrayList<>();
            for(AddressPO addressPO: addressPOList){
                AddressVO addressVO = new AddressVO();
                BeanUtils.copyProperties(addressPO, addressVO);
                addressVOList.add(addressVO);
            }
            return ResultEntity.successWithData(addressVOList);
        }catch (Exception e){
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/save/address/vo/remote")
    ResultEntity<String> saveAddress(@RequestBody AddressVO addressVO){
        try{
            AddressPO addressPO = new AddressPO();
            BeanUtils.copyProperties(addressVO, addressPO);
            orderService.saveAddressPO(addressPO);
            return ResultEntity.successWithoutData();
        }catch (Exception e){
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/save/order/remote")
    ResultEntity<String> saveOrderRemote(@RequestBody OrderVO orderVO){
        try {
            orderService.saveOrder(orderVO);
            return ResultEntity.successWithoutData();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }
}
