package com.ego.gateway.filter;

import com.ego.auth.utils.JwtUtils;
import com.ego.commom.utils.CookieUtils;
import com.ego.gateway.config.FilterProperties;
import com.ego.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Component
@EnableConfigurationProperties({JwtProperties.class, FilterProperties.class})
public class LoginFilter extends ZuulFilter {

    @Autowired
    private JwtProperties jwtProp;
    @Autowired
    private FilterProperties filterProp;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = context.getRequest();
        // 获取路径
        String requestURI = req.getRequestURI();
        // 判断白名单
        boolean b = filterProp.getAllowPaths().stream().anyMatch(path -> requestURI.startsWith(path));
        return !b;

    }

    @Override
    public Object run() throws ZuulException {
        // 获取上下文
        RequestContext context = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = context.getRequest();
        // 获取token
        String token = CookieUtils.getCookieValue(request,jwtProp.getCookieName());
        // 校验
        try {
            // 校验通过什么都不做，即放行
            JwtUtils.getInfoFromToken(token,jwtProp.getPublicKey());
        } catch (Exception e) {
            // 关闭发送zuul自动响应500
            context.setSendZuulResponse(false);
            // 校验出现异常，返回403
            context.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
        }
        return null;
    }
}
