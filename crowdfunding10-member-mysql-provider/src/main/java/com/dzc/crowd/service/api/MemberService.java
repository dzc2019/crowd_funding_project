package com.dzc.crowd.service.api;

import com.dzc.crowd.entity.po.MemberPO;


public interface MemberService {
    MemberPO getMemberPOByLoginAcct(String loginacct);

    void saveMember(MemberPO memberPO);
}
