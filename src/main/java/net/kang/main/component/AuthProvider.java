package net.kang.main.component;

import net.kang.main.domain.User;
import net.kang.main.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AuthProvider implements AuthenticationProvider {
    @Autowired UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginId = authentication.getName();
        String passwd = authentication.getCredentials().toString();
        return authenticate(loginId, passwd);
    }

    public Authentication authenticate(String loginId, String passwd) throws AuthenticationException{
        User user = userService.login(loginId, passwd);
        if(user == null) return null;

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        String role = "";
        switch(user.getRole().getName()){
            case "ADMIN" :
                role = "ROLE_ADMIN";
                break;
            case "MANAGER" :
                role = "ROLE_MANAGER";
                break;
            case "USER" :
                role = "ROLE_USER";
                break;
        }
        grantedAuthorities.add(new SimpleGrantedAuthority(role));
        return new TokenAuthentication(loginId, passwd, grantedAuthorities, user);
    }

    @Override
    public boolean supports(Class<?> authentication){
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public class TokenAuthentication extends UsernamePasswordAuthenticationToken{
        private static final long serialVersionUID = 1L;
        User user;

        public TokenAuthentication(String loginId, String passwd, List<GrantedAuthority> grantedAuthorities, User user){
            super(loginId, passwd, grantedAuthorities);
            this.user = user;
        }

        public User getUser(){
            return user;
        }

        public void setUser(User user){
            this.user = user;
        }
    }
}
