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
import net.kang.main.model.NameEmailVO;
import net.kang.main.model.SignVO;
import net.kang.main.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.Filter;
import javax.servlet.ServletException;

import java.time.LocalDateTime;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// 비회원을 위한 REST Controller를 확인하는 테스팅 클래스이다.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@WebAppConfiguration
@SpringBootTest(classes = {net.kang.main.RestSpringSecurityApplication.class})
public class GuestRestControllerTest {
    MockMvc mockMvc;

    // Security Configuration을 위해 이 요소들은 Mock으로 설정해둬야 한다.
    @Mock AuthProvider authProvider;
    @Mock AuthenticationEntryPoint authenticationEntryPoint;
    @Mock AuthLoginSuccessHandler authLoginSuccessHandler;
    @Mock MyAccessDeniedHandler myAccessDeniedHandler;

    // UserService는 별도로 Mock으로 설정한다.
    @Mock UserService userService;

    // GuestRestController는 Mock으로 설정하지 않는다.
    @InjectMocks GuestRestController guestRestController;

    @Autowired Filter springSecurityFilterChain;

    // 일반 객체를 JSON으로 반환하기 위하여 LocalDateTime 설정을 따로 한 뒤에 반환해야 한다.
    private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JSR310Module())
                .build();
        return objectMapper.writeValueAsString(object);
    }

    // SignVO 객체를 만들기 위한 문장
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

    // NameEmailVO를 만들기 위한 문장
    private NameEmailVO createNameEmailVO(String name, String email){
        NameEmailVO nameEmailVO = new NameEmailVO();
        nameEmailVO.setName(name);
        nameEmailVO.setEmail(email);
        return nameEmailVO;
    }

    // 테스팅 전에 초기화하는 과정. 실행 결과는 항상 출력되도록 설정한다.
    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(guestRestController)
                .addFilters()
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();

        MockitoAnnotations.initMocks(this);
    }

    // Guest 처음 페이지 접근 테스팅
    @Test
    public void guest_main() throws Exception {
        mockMvc
                .perform(get("/guest/main"))
                .andExpect(status().isOk())
                .andReturn();
    }

    // 회원 가입 성공 여부 테스팅
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

    // 회원 가입 실패 여부 테스팅
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

    // 회원 가입 중 예외 처리 테스팅
    @Test(expected = ServletException.class)
    public void guest_sign_exception() throws Exception{
        SignVO signVO = this.createSignVO("test3", "testing", "testing", "tester", "tester@test.com", LocalDateTime.now(), "suwon");
        doThrow(new ServletException("Username is Existed. Try Again.")).when(userService).create(signVO);
        mockMvc
                .perform(post("/guest/sign")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(signVO))
                ).andExpect(status().isInternalServerError());
    }

    // 회원 ID 조회 성공 테스팅
    @Test
    public void guest_find_username_success() throws Exception{
        NameEmailVO nameEmailVO = this.createNameEmailVO("tester", "tester@test.com");
        when(userService.findByNameAndEmail(nameEmailVO)).thenReturn("tester_id");
        mockMvc
                .perform(post("/guest/find_username")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(nameEmailVO))
                ).andExpect(status().isOk());
    }

    // 회원 ID 조회 실패 테스팅
    @Test
    public void guest_find_username_failure() throws Exception{
        NameEmailVO nameEmailVO = this.createNameEmailVO("tester", "tester@test.com");
        when(userService.findByNameAndEmail(nameEmailVO)).thenReturn(null);
        mockMvc
                .perform(post("/guest/find_username")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(nameEmailVO))
                ).andExpect(status().isNotFound());
    }
}
