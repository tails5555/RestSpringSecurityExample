package net.kang.main.controller_unit;

import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.UserRestController;
import net.kang.main.exception.MyAccessDeniedHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.Filter;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@WebAppConfiguration
@SpringBootTest(classes = {net.kang.main.RestSpringSecurityApplication.class})
public class UserRestControllerTest {
    MockMvc mockMvc;

    @Mock AuthProvider authProvider;
    @Mock AuthenticationEntryPoint authenticationEntryPoint;
    @Mock AuthLoginSuccessHandler authLoginSuccessHandler;
    @Mock MyAccessDeniedHandler myAccessDeniedHandler;

    @InjectMocks UserRestController userRestController;
    @Autowired Filter springSecurityFilterChain;

    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(userRestController)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"USER"})
    public void user_access_process_success() throws Exception{
        mockMvc.perform(get("/user/login_process")
                        .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void user_access_process_forbidden() throws Exception{
        mockMvc.perform(get("/user/login_process")
                        .with(csrf())
                        .with(httpBasic("tester", "test123")))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithAnonymousUser
    public void user_access_process_unauthorized() throws Exception{
        mockMvc.perform(get("/user/login_process")
                        .with(csrf()))
                .andExpect(status().isUnauthorized());
    }
}
