package com.dzc.crowd.service.impl;

import com.dzc.crowd.entity.po.AddressPO;
import com.dzc.crowd.entity.po.AddressPOExample;
import com.dzc.crowd.entity.po.OrderPO;
import com.dzc.crowd.entity.po.OrderProjectPO;
import com.dzc.crowd.entity.vo.AddressVO;
import com.dzc.crowd.entity.vo.OrderProjectVO;
import com.dzc.crowd.entity.vo.OrderVO;
import com.dzc.crowd.mapper.AddressPOMapper;
import com.dzc.crowd.mapper.OrderPOMapper;
import com.dzc.crowd.mapper.OrderProjectPOMapper;
import com.dzc.crowd.service.api.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderProjectPOMapper orderProjectPOMapper;
    @Autowired
    private AddressPOMapper addressPOMapper;
    @Autowired
    private OrderPOMapper orderPOMapper;

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    @Override
    public OrderProjectVO getOrderProjectVO(Integer projectId, Integer returnId) {
        return orderProjectPOMapper.selectOrderProjectVO(returnId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    @Override
    public List<AddressPO> getAddressPOList(Integer memberId) {
        AddressPOExample addressPOExample = new AddressPOExample();
        addressPOExample.createCriteria().andMemberIdEqualTo(memberId);
        return addressPOMapper.selectByExample(addressPOExample);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    @Override
    public void saveAddressPO(AddressPO addressPO) {
        addressPOMapper.insertSelective(addressPO);
    }

    @Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    @Override
    public void saveOrder(OrderVO orderVO) {
        OrderPO orderPO = new OrderPO();
        BeanUtils.copyProperties(orderVO, orderPO);
        OrderProjectPO orderProject = new OrderProjectPO();
        BeanUtils.copyProperties(orderVO.getOrderProjectVO(), orderProject);
        orderPOMapper.insert(orderPO);
        Integer id = orderPO.getId();
        orderProject.setOrderId(id);
        orderProjectPOMapper.insert(orderProject);
    }
}
