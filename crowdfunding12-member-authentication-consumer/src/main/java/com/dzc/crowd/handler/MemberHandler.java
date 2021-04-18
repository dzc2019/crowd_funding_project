package com.dzc.crowd.handler;

import com.dzc.crowd.api.MySQLRemoteService;
import com.dzc.crowd.api.RedisRemoteService;
import com.dzc.crowd.config.ShortMessageProperties;
import com.dzc.crowd.entity.po.MemberPO;
import com.dzc.crowd.entity.vo.MemberLoginVO;
import com.dzc.crowd.entity.vo.MemberVO;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.util.CrowdUtil;
import com.zcdeng.crowd.util.ResultEntity;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Controller
public class MemberHandler {
    @Autowired
    private ShortMessageProperties shortMessageProperties;

    @Autowired
    private RedisRemoteService redisRemoteService;

    @Autowired
    private MySQLRemoteService mySQLRemoteService;

    @ResponseBody
    @RequestMapping("/auth/member/send/short/message.json")
    public ResultEntity<String> sendMessage(@RequestParam(value = "phoneNum") String phoneNumber) {
        ResultEntity<String> result = CrowdUtil.sendShortMessage(shortMessageProperties.getHost(),
                shortMessageProperties.getPath(),
                shortMessageProperties.getMethod(),
                shortMessageProperties.getAppcode(),
                phoneNumber);
        if (ResultEntity.SUCCESS.equals(result.getOperationResult())) {
            String code = result.getQueryData();
            String key = CrowdConstant.REDIS_CODE_PREFIX + phoneNumber;
            ResultEntity<String> saveResult = redisRemoteService.setRedisKeyValueRemoteWithTimeout(key, code, 5, TimeUnit.MINUTES);
            if (ResultEntity.SUCCESS.equals(saveResult.getOperationResult())) {
                return ResultEntity.successWithoutData();
            }
            return saveResult;
        }
        return result;
    }

    @RequestMapping("/auth/do/member/register")
    public String registerMember(MemberVO memberVO,
                                 ModelMap modelMap) {
        String key = CrowdConstant.REDIS_CODE_PREFIX + memberVO.getPhoneNum();
        ResultEntity<String> getKeyResult = redisRemoteService.getRedisStringValueByKeyRemote(key);
        modelMap.addAttribute(CrowdConstant.ATTR_NAME_REGISTER_MEMBERVO, memberVO);
        if (ResultEntity.FAILED.equals(getKeyResult.getOperationResult())) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, getKeyResult.getOperationMessage());
            return "member-reg";
        }
        String queryData = getKeyResult.getQueryData();
        if (queryData == null) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_CODE_NOT_EXIST);
            return "member-reg";
        }
        if (!Objects.equals(queryData, memberVO.getAuthCode())) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_ERROR);
            return "member-reg";
        }

        MemberPO memberPO = new MemberPO();
        BeanUtils.copyProperties(memberVO, memberPO);

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encodedPassword = bCryptPasswordEncoder.encode(memberPO.getUserpswd());
        memberPO.setUserpswd(encodedPassword);

        ResultEntity<String> saveResult = mySQLRemoteService.saveMemberPO(memberPO);
        if (ResultEntity.FAILED.equals(saveResult.getOperationResult())) {
            modelMap.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, saveResult.getOperationMessage());
            return "member-reg";
        }
        modelMap.remove(CrowdConstant.ATTR_NAME_REGISTER_MEMBERVO);
        redisRemoteService.removeRedisKeyRemote(key);
        return "redirect:http://localhost:8081/auth/member/to/login/page";
    }

    @RequestMapping("/auth/do/member/login")
    public String doLogin(@RequestParam("loginacct") String loginacct,
                          @RequestParam("userpswd") String userpswd,
                          ModelMap map,
                          HttpSession session) {
        ResultEntity<MemberPO> memberPOResultEntity = mySQLRemoteService.getMemberPOByLoginAcctRemote(loginacct);
        if (ResultEntity.FAILED.equals(memberPOResultEntity.getOperationResult())) {
            map.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, memberPOResultEntity.getOperationMessage());
            return "member-login";
        }

        MemberPO memberPO = memberPOResultEntity.getQueryData();
        if(memberPO == null){
            map.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }

        String realpwsd = memberPO.getUserpswd();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        boolean matches = bCryptPasswordEncoder.matches(userpswd, realpwsd);

        if (!matches) {
            map.addAttribute(CrowdConstant.ATTR_NAME_MESSAGE, CrowdConstant.MESSAGE_LOGIN_FAILED);
            return "member-login";
        }
        MemberLoginVO memberLoginVO = new MemberLoginVO(memberPO.getId(), memberPO.getUsername(), memberPO.getEmail());
        //session.invalidate();
        session.setAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER, memberLoginVO);
        return "redirect:http://localhost:8081/auth/member/to/center/page";
    }

    @RequestMapping("/auth/do/member/logout")
    public String doLogout(HttpSession session) {
        session.invalidate();
        return "redirect:http://localhost:8081/auth/member/to/login/page";
    }
}
