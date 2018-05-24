package net.kang.main.exception;

import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.springframework.security.access.AccessDeniedException;

@Component
public class MyAccessDeniedHandler implements AccessDeniedHandler {
    private static String REALM = "MY_STACK";

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException ade) throws IOException, ServletException {
        response.addHeader("WWW-Authenticate", String.format("Basic realm=\"%s\"", REALM));
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        PrintWriter writer = response.getWriter();
        writer.println(String.format("HTTP STATUS 403 - %s", ade.getMessage()));
    }
}
