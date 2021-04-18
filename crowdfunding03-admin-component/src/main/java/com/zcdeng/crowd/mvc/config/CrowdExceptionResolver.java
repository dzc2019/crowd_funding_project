package com.zcdeng.crowd.mvc.config;

import com.google.gson.Gson;
import com.zcdeng.crowd.exception.LoginAcctAlreadyInUseException;
import com.zcdeng.crowd.exception.LoginAcctAlreadyInUseForUpdateException;
import com.zcdeng.crowd.exception.LoginFailedException;
import com.zcdeng.crowd.constant.CrowdConstant;
import com.zcdeng.crowd.util.ResultEntity;
import com.zcdeng.crowd.util.CrowdUtil;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@ControllerAdvice
public class CrowdExceptionResolver {
    //使用注解时无法捕获拦截器的异常
//    @ExceptionHandler(value = AccessForbiddenException.class)
//    public ModelAndView resolveAccessForbiddenException(AccessForbiddenException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
//        String viewName = "admin-login";
//        return commonResolve(e, request, response, viewName);
//    }
    @ExceptionHandler(value = Exception.class)
    public ModelAndView resolveException(Exception exception,
                                         HttpServletRequest request,
                                         HttpServletResponse response
    ) throws IOException {
        String viewName = "system-error";
        return commonResolve(exception, request, response, viewName);
    }

    @ExceptionHandler(value = LoginAcctAlreadyInUseForUpdateException.class)
    public ModelAndView resolveLoginAcctAlreadyInUseException(
            LoginAcctAlreadyInUseForUpdateException exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String viewName = "admin-edit";
        return commonResolve(exception, request, response, viewName);
    }

    @ExceptionHandler(value = LoginAcctAlreadyInUseException.class)
    public ModelAndView resolveLoginAcctAlreadyInUseException(
            LoginAcctAlreadyInUseException exception,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException {
        String viewName = "admin-add";
        return commonResolve(exception, request, response, viewName);
    }

    @ExceptionHandler(value = LoginFailedException.class)
    public ModelAndView resolveLoginFailedException(LoginFailedException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        String viewName = "admin-login";
        return commonResolve(e, request, response, viewName);
    }

    @ExceptionHandler(value = ArithmeticException.class)
    public ModelAndView resolveArithmeticException(ArithmeticException e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        return commonResolve(e, request, response, "system-error");
    }

    private ModelAndView commonResolve(Exception e, HttpServletRequest request, HttpServletResponse response, String viewName) throws IOException {
        if (!CrowdUtil.isAjaxRequest(request)) {
            ModelAndView mv = new ModelAndView();


            mv.addObject(CrowdConstant.ATTR_NAME_EXCEPTION, e);
            mv.setViewName(viewName);
            return mv;
        } else {
            ResultEntity<Object> failed = ResultEntity.failed(e.getMessage());
            Gson gson = new Gson();
            String json = gson.toJson(failed);
            response.getWriter().write(json);
            return null;
        }
    }
}
