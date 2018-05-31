package net.kang.main.controller_unit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.GuestRestController;
import net.kang.main.exception.MyAccessDeniedHandler;
import net.kang.main.model.SignVO;
import net.kang.main.model.UserVO;
import net.kang.main.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.Filter;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@WebAppConfiguration
@SpringBootTest
public class GuestRestControllerTest {
    MockMvc mockMvc;

    @MockBean AuthProvider authProvider;
    @MockBean AuthenticationEntryPoint authenticationEntryPoint;
    @MockBean AuthLoginSuccessHandler authLoginSuccessHandler;
    @MockBean MyAccessDeniedHandler myAccessDeniedHandler;

    @InjectMocks GuestRestController guestRestController;
    @Mock UserService userService;

    @Autowired Filter springSecurityFilterChain;

    private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL) // Don’t include null values
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) //ISODate
                .modules(new JSR310Module())
                .build();
        return objectMapper.writeValueAsString(object);
    }

    private SignVO createSignVO(String username, String password_1, String password_2, String name, String email, LocalDateTime birthday, String address){
        SignVO signVO = new SignVO();
        signVO.setUsername(username);
        signVO.setPassword_1(password_1);
        signVO.setPassword_2(password_2);
        signVO.setName(name);
        signVO.setEmail(email);
        signVO.setBirthday(birthday);
        signVO.setAddress(address);

        return signVO;
    }

    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(guestRestController)
                .addFilters()
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
    }

    @Test
    public void guest_main() throws Exception {
        mockMvc
                .perform(get("/guest/main"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    public void guest_sign_success() throws Exception{
        SignVO signVO = this.createSignVO("test1", "testing", "testing", "tester", "tester@test.com", LocalDateTime.now(), "suwon");
        when(userService.create(signVO)).thenReturn(true);

        mockMvc
                .perform(post("/guest/sign")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(signVO))
                )
                .andExpect(status().isCreated());
    }

    @Test
    public void guest_sign_failure() throws Exception{
        SignVO signVO = this.createSignVO("test2", "testin", "testing", "tester", "tester@test.com", LocalDateTime.now(), "suwon");
        when(userService.create(signVO)).thenReturn(false);

        mockMvc
                .perform(post("/guest/sign")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(signVO))
                )
                .andExpect(status().isNotModified());
    }

    @Test(expected = AuthenticationServiceException.class)
    public void guest_sign_exception() throws Exception{
        SignVO signVO = this.createSignVO("test3", "testing", "testing", "tester", "tester@test.com", LocalDateTime.now(), "suwon");
        when(userService.create(signVO)).thenThrow(new AuthenticationServiceException("Username is Existed. Try Again."));
        mockMvc
                .perform(post("/guest/sign")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(signVO))
                )
                .andExpect(status().isUnauthorized());
    }
}
