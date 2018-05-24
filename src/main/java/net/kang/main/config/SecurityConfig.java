package net.kang.main.config;

import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired AuthProvider authProvider;
    @Autowired AuthenticationEntryPoint authEntryPoint;
    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/admin/**").access("ROLE_ADMIN")
                .antMatchers("/manager/**").access("ROLE_MANAGER")
                .antMatchers("/user/**").access("ROLE_USER")
                .antMatchers("/guest/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/**").authenticated()
                .and().httpBasic()
                .authenticationEntryPoint(authEntryPoint);

        http.csrf().disable();

        http.formLogin()
            .and()
            .logout();

        // http.authenticationProvider(authProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
