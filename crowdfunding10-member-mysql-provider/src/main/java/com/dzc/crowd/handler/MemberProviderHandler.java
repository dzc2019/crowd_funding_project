package com.dzc.crowd.handler;

import com.dzc.crowd.entity.po.MemberPO;
import com.dzc.crowd.service.api.MemberService;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.util.CrowdUtil;
import com.zcdeng.crowd.util.ResultEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberProviderHandler {

    @Autowired
    private MemberService memberService;

    @RequestMapping("/get/memberpo/by/login/acct/remote")
    public ResultEntity<MemberPO> getMemberPOByLoginAcctRemote(@RequestParam("loginacct") String loginacct) {
        try {
            MemberPO memberPO = memberService.getMemberPOByLoginAcct(loginacct);
            return ResultEntity.successWithData(memberPO);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultEntity.failed(e.getMessage());
        }
    }

    @RequestMapping("/save/memberpo/remore")
    public ResultEntity<String> saveMemberPO(@RequestBody MemberPO memberPO){
        try{
            memberService.saveMember(memberPO);
            return ResultEntity.successWithoutData();
        }catch (DuplicateKeyException e){
            return ResultEntity.failed(CrowdConstant.MESSAGE_LOGIN_ACCT_ALREADY_IN_USE);
        }catch (Exception e){
            return ResultEntity.failed(e.getMessage());
        }
    }

}
