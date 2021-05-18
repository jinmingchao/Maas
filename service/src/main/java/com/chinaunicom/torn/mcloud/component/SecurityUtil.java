package com.chinaunicom.torn.mcloud.component;

import com.alibaba.fastjson.JSONObject;
import com.chinaunicom.torn.mcloud.config.LoginRedirectConfig;
import org.apache.http.Consts;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.Collection;

@Component
public class SecurityUtil {

    @Autowired
    UserDetailsService userDetailsService;

    @Autowired
    private LoginRedirectConfig loginConfig;

    public boolean pass() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        HttpSession session = request.getSession();

        String location = request.getRequestURL().toString();

        if (this.loginConfig.getActive().equals("prod") && location.contains("http") && !location.contains("https")) {
            location = location.replace("http", "https");
        }

        if (session.getAttribute("Username") != null) {
            if (request.getParameter("bk_token") != null)  {
                try {
                    response.sendRedirect(location);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }

            this.logInAs(session.getAttribute("Username").toString());

            return true;
        }
        else if (this.loginConfig.getDev()) {
            this.logInAs(this.loginConfig.getDevUsername());
            return true;
        }
        else if (request.getParameter("bk_token") != null) {
            session.setAttribute("BKToken", request.getParameter("bk_token").toString());

            if (this.passBKLogin(request.getParameter("bk_token").toString())) {

                try {
                    response.sendRedirect(location);
                }
                catch (Exception ex) {
                    ex.printStackTrace();
                }
                return false;
            }
        }

        try {
            location = URLEncoder.encode(location, "UTF-8");
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        response.setStatus(HttpStatus.MOVED_PERMANENTLY.value());
        response.setHeader("Location", String.format("%s?c_url=%s", loginConfig.getBkLoginRedirect(), location));
        response.setHeader("Access-Control-Allow-Origin", "*");

        return false;
    }

    public boolean passBKLogin(String bkToken) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        CloseableHttpClient client = HttpClients.custom().build();
        HttpGet getMethod = new HttpGet(String.format("%sapi/v2/is_login?%s=%s", this.loginConfig.getApi(), this.loginConfig.getCookieKey(), bkToken));

        try {
            CloseableHttpResponse response = client.execute(getMethod);

            String responseString = EntityUtils.toString(response.getEntity(), Consts.UTF_8);
            JSONObject responseJson = JSONObject.parseObject(responseString);
            if (responseJson.getBooleanValue("result")) {
                session.setAttribute("Username", responseJson.getJSONObject("data").getString("bk_username"));
                this.logInAs(session.getAttribute("Username").toString());
                return true;
            }
            else {
                return false;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public void logInAs(String username) {
        UserDetails user = this.userDetailsService.loadUserByUsername(username);

        SecurityContextHolder.setContext(new SecurityContextImpl(new Authentication() {

            private static final long serialVersionUID = 1L;

            @Override
            public Collection<? extends GrantedAuthority> getAuthorities() {
                return user.getAuthorities();
            }

            @Override
            public Object getCredentials() {
                return user.getPassword();
            }

            @Override
            public Object getDetails() {
                return user;
            }

            @Override
            public Object getPrincipal() {
                return user;
            }

            @Override
            public boolean isAuthenticated() {
                return true;
            }

            @Override
            public void setAuthenticated(boolean isAuthorized) throws IllegalArgumentException {
            }

            @Override
            public String getName() {
                return user.getUsername();
            }
        }));
    }

    public void logout() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        session.removeAttribute("Username");
        session.removeAttribute("BKToken");
    }
}
