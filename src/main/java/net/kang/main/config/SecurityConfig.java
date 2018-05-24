package net.kang.main.config;

import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.exception.MyAccessDeniedHandler;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired MyAccessDeniedHandler myAccessDeniedHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception{
        http.authorizeRequests()
                .antMatchers("/admin/**").hasRole("ADMIN")
                .antMatchers("/manager/**").hasRole("MANAGER")
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/guest/**").permitAll()
                .antMatchers("/").permitAll()
                .antMatchers("/**").authenticated()
                .and()
                .httpBasic().authenticationEntryPoint(authEntryPoint)
                .and()
                .exceptionHandling().accessDeniedHandler(myAccessDeniedHandler);

        http.csrf().disable();

        http.formLogin()
            .and()
            .logout();

        http.authenticationProvider(authProvider);
    }

    @Override
    public void configure(WebSecurity web) throws Exception{
        web.ignoring().antMatchers(HttpMethod.OPTIONS, "/**");
    }
}
