package net.kang.main.component;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class AuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
    private static String REALM = "MY_STACK";

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException ae) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", String.format("Basic realm=\"%s\"", getRealmName()));
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        PrintWriter writer = response.getWriter();
        writer.println(String.format("HTTP STATUS 401 - %s", ae.getMessage()));
    }

    @Override
    public void afterPropertiesSet() throws Exception{
        setRealmName(REALM);
        super.afterPropertiesSet();
    }
}
