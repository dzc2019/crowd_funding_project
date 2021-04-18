package com.dzc.crowd.service.api;

import com.dzc.crowd.entity.po.AddressPO;
import com.dzc.crowd.entity.vo.AddressVO;
import com.dzc.crowd.entity.vo.OrderProjectVO;
import com.dzc.crowd.entity.vo.OrderVO;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    OrderProjectVO getOrderProjectVO(Integer projectId, Integer returnId);

    List<AddressPO> getAddressPOList(Integer memberId);

    void saveAddressPO(AddressPO addressPO);

    void saveOrder(OrderVO orderVO);
}
