package net.kang.main.component;

import net.kang.main.enums.RoleType;
import net.kang.main.domain.Role;
import net.kang.main.model.UserVO;
import net.kang.main.service.LoginService;
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
public class AuthProvider implements AuthenticationProvider{
    @Autowired LoginService loginService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String loginId = authentication.getName();
        String passwd = authentication.getCredentials().toString();
        return authenticate(loginId, passwd);
    }

    public Authentication authenticate(String loginId, String passwd) throws AuthenticationException{
        UserVO user = loginService.login(loginId, passwd);
        if(user == null) return null;

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();

        String role = "";
        for(Role r : user.getRoles()) {
            switch (r.getName()) {
                case "ADMIN":
                    role = RoleType.ADMIN.getRoleType();
                    break;
                case "MANAGER":
                    role = RoleType.MANAGER.getRoleType();
                    break;
                case "USER":
                    role = RoleType.USER.getRoleType();
                    break;
            }
            grantedAuthorities.add(new SimpleGrantedAuthority(role));
        }
        return new TokenAuthentication(loginId, passwd, grantedAuthorities, user);
    }

    @Override
    public boolean supports(Class<?> authentication){
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    public class TokenAuthentication extends UsernamePasswordAuthenticationToken{
        private static final long serialVersionUID = 1L;
        UserVO user;

        public TokenAuthentication(String loginId, String passwd, List<GrantedAuthority> grantedAuthorities, UserVO user){
            super(loginId, passwd, grantedAuthorities);
            this.user = user;
        }

        public UserVO getUser(){
            return user;
        }

        public void setUserVO(UserVO user){
            this.user = user;
        }
    }
}
