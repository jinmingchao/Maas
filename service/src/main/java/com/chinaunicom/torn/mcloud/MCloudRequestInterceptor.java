package com.chinaunicom.torn.mcloud;

import com.chinaunicom.torn.mcloud.component.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Configuration
public class MCloudRequestInterceptor implements WebMvcConfigurer {

    @Autowired
    private SecurityUtil securityUtil;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
                    throws Exception {
                response.setHeader("Expires", "-1");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                if (request.getRequestURI().matches(".*(/api/kickstart).*")) return true;
                boolean result = securityUtil.pass();
                return result;
            }
        }).addPathPatterns("/**");
    }
}
