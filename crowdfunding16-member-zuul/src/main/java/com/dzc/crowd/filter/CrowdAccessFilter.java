package com.dzc.crowd.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.zcdeng.crowd.constant.AccessPassResources;
import com.zcdeng.crowd.constant.CrowdConstant;
import org.springframework.stereotype.Component;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CrowdAccessFilter extends ZuulFilter {
    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String servletPath = request.getServletPath();
        if (AccessPassResources.PASS_RES_SET.contains(servletPath)) {
            return false;
        }

        return !AccessPassResources.judgeCurrentServletPathWetherStaticResource(servletPath);
    }

    @Override
    public Object run() throws ZuulException {
        // 1.获取当前请求对象
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        // 2.获取当前 Session 对象
        HttpSession session = request.getSession();
        // 3.尝试从 Session 对象中获取已登录的用户
        Object loginMember = session.getAttribute(CrowdConstant.ATTR_NAME_LOGIN_MEMBER);
        // 4.判断 loginMember 是否为空
        if (loginMember == null) {
            // 5.从 requestContext 对象中获取 Response 对象
            HttpServletResponse response = requestContext.getResponse();
            // 6.将提示消息存入 Session 域
            session.setAttribute(CrowdConstant.ATTR_NAME_MESSAGE,
                    CrowdConstant.MESSAGE_ACCESS_FORBIDEN);
            // 7.重定向到 auth-consumer 工程中的登录页面
            try {
                response.sendRedirect("/auth/member/to/login/page");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
