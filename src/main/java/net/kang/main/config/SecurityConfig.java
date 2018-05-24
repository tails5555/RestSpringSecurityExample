package net.kang.main.config;

import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.exception.MyAccessDeniedHandler;
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
    @Autowired AuthLoginSuccessHandler authLoginSuccessHandler;
    @Autowired MyAccessDeniedHandler myAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/manager/**").hasRole("MANAGER")
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/guest/**").permitAll()
                .antMatchers("/").permitAll();

        http
            .csrf().disable();

        http
            .httpBasic()
                .authenticationEntryPoint(authEntryPoint)
            .and()
                .exceptionHandling().accessDeniedHandler(myAccessDeniedHandler);



        http.authenticationProvider(authProvider);

        http.formLogin()
                .successHandler(authLoginSuccessHandler)
            .and()
            .logout()
                .logoutUrl("/**/logout");
    }

    @Bean
    public AuthLoginSuccessHandler authLoginSuccessHandler() {
        return new AuthLoginSuccessHandler();
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
