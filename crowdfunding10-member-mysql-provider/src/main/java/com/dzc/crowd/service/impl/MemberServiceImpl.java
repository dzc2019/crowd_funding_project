package com.dzc.crowd.service.impl;

import com.dzc.crowd.entity.po.MemberPO;
import com.dzc.crowd.entity.po.MemberPOExample;
import com.dzc.crowd.entity.vo.DetailProjectVO;
import com.dzc.crowd.mapper.MemberPOMapper;
import com.dzc.crowd.service.api.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberPOMapper memberPOMapper;

    @Override
    public MemberPO getMemberPOByLoginAcct(String loginacct) {
        MemberPOExample memberPOExample = new MemberPOExample();
        memberPOExample.createCriteria().andLoginacctEqualTo(loginacct);
        List<MemberPO> memberPOS = memberPOMapper.selectByExample(memberPOExample);
        if(memberPOS.isEmpty()) return null;
        return memberPOS.get(0);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = false)
    @Override
    public void saveMember(MemberPO memberPO) {
        memberPOMapper.insertSelective(memberPO);
    }


}
