package net.kang.main.controller_unit;

import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.GuestRestController;
import net.kang.main.exception.MyAccessDeniedHandler;
import net.kang.main.model.SignVO;
import net.kang.main.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.Filter;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@SpringBootTest
public class GuestRestControllerTest {
    MockMvc mockMvc;
    @MockBean AuthProvider authProvider;
    @MockBean AuthenticationEntryPoint authenticationEntryPoint;
    @MockBean AuthLoginSuccessHandler authLoginSuccessHandler;
    @MockBean MyAccessDeniedHandler myAccessDeniedHandler;

    @MockBean GuestRestController guestRestController;
    @MockBean UserService userService;

    @Autowired Filter springSecurityFilterChain;

    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(guestRestController)
                .addFilters(springSecurityFilterChain)
                .build();
    }

    @Test
    public void guest_main() throws Exception {

        mockMvc
                .perform(get("/guest/main"))
                .andExpect(status().isOk());
    }

    @Test
    public void guest_sign_success() throws Exception{
        SignVO signVO = new SignVO();
        signVO.setUsername("test1");
        signVO.setPassword_1("testing");
        signVO.setPassword_2("testing");
        signVO.setAddress("suwon");
        signVO.setName("tester");
        signVO.setBirthday(LocalDateTime.now());
        signVO.setEmail("tester@test.com");

        mockMvc
                .perform(post("/guest/sign")
                .requestAttr("signVO", signVO))
                .andExpect(status().isCreated());
    }
}
