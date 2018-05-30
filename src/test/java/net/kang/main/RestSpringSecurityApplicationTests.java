package net.kang.main;

import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.GuestRestController;
import net.kang.main.exception.MyAccessDeniedHandler;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@SpringBootTest
public class RestSpringSecurityApplicationTests {
    MockMvc mockMvc;
    @MockBean AuthProvider authProvider;
    @MockBean AuthenticationEntryPoint authenticationEntryPoint;
    @MockBean AuthLoginSuccessHandler authLoginSuccessHandler;
    @MockBean MyAccessDeniedHandler myAccessDeniedHandler;

    @MockBean GuestRestController guestRestController;

    @Autowired WebApplicationContext webApplicationContext;
    @Autowired Filter springSecurityFilterChain;

}
