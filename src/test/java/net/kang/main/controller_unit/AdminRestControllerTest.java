package net.kang.main.controller_unit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.AdminRestController;
import net.kang.main.domain.Role;
import net.kang.main.exception.MyAccessDeniedHandler;
import net.kang.main.model.UserVO;
import net.kang.main.service.UserService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.Filter;
import javax.servlet.ServletException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// ADMIN 회원을 위한 REST Controller를 확인하는 테스팅 클래스이다.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@WebAppConfiguration
@SpringBootTest(classes = {net.kang.main.RestSpringSecurityApplication.class})
public class AdminRestControllerTest {
    MockMvc mockMvc;

    // Security Configuration을 위해 이 요소들은 Mock으로 설정해둬야 한다.
    @Mock AuthProvider authProvider;
    @Mock AuthenticationEntryPoint authenticationEntryPoint;
    @Mock AuthLoginSuccessHandler authLoginSuccessHandler;
    @Mock MyAccessDeniedHandler myAccessDeniedHandler;

    // UserService는 별도로 Mock으로 설정한다.
    @Mock UserService userService;

    // AdminRestController는 Mock으로 설정하지 않는다.
    @InjectMocks AdminRestController adminRestController;

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

    // UserVO 객체를 만들기 위한 문장
    private UserVO createUserVO(String username, String name, String email, LocalDateTime birthday, String address, List<Role> roles){
        UserVO userVO = new UserVO(username, name, email, birthday, address, roles);
        return userVO;
    }

    // 테스팅 전에 초기화하는 과정. 실행 결과는 항상 출력되도록 설정한다.
    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(adminRestController)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();
        MockitoAnnotations.initMocks(this);
    }

    // ADMIN 권한 접근 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_access_process_success() throws Exception{
        mockMvc.perform(get("/admin/login_process")
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // ADMIN 권한 접근 금지 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"MANAGER"})
    public void admin_access_process_forbidden() throws Exception{
        mockMvc.perform(get("/admin/login_process")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isForbidden());
    }

    // 비회원 접근 방지 테스팅
    @Test
    @WithAnonymousUser
    public void admin_access_process_unauthorized() throws Exception{
        mockMvc.perform(get("/admin/login_process")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // ADMIN 회원 목록 조회 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_find_all_users_success() throws Exception{
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("USER");
        Role role2 = new Role();
        role2.setId(2);
        role2.setName("MANAGER");
        Role role3 = new Role();
        role3.setId(3);
        role3.setName("ADMIN");

        when(userService.findAll()).thenReturn(
                Arrays.asList(
                        this.createUserVO("tester", "tester", "tester@test.com", LocalDateTime.now(), "seoul", Arrays.asList(role3)),
                        this.createUserVO("tester1", "tester1", "tester1@test.com", LocalDateTime.now(), "suwon", Arrays.asList(role1)),
                        this.createUserVO("tester2", "tester2", "tester2@test.com", LocalDateTime.now(), "bucheon", Arrays.asList(role1, role2))
                )
        );

        mockMvc.perform(get("/admin/all_users")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // ADMIN 회원 목록 조회 실패 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_find_all_users_failure() throws Exception{
        when(userService.findAll()).thenReturn(new ArrayList<UserVO>());
        mockMvc.perform(get("/admin/all_users")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }

    // ADMIN 회원 수 파악 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_counting_success() throws Exception{
        when(userService.count()).thenReturn(500L);
        mockMvc.perform(get("/admin/count")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // ADMIN 회원 수 파악 실패 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_counting_failure() throws Exception{
        when(userService.count()).thenReturn(0L);
        mockMvc.perform(get("/admin/count")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }

    // ADMIN 권한 별 회원 수 파악 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_counting_with_role() throws Exception{
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("USER");
        Role role2 = new Role();
        role2.setId(2);
        role2.setName("MANAGER");
        Role role3 = new Role();
        role3.setId(3);
        role3.setName("ADMIN");

        Map<Role, Long> map = new HashMap<Role, Long>();
        map.put(role1, 30L);
        map.put(role2, 5L);
        map.put(role3, 1L);

        when(userService.countWithAll()).thenReturn(map);
        mockMvc.perform(get("/admin/count/role")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // ADMIN 회원 강퇴 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_delete_user_success() throws Exception{
        when(userService.delete("tester_delete")).thenReturn(true);
        mockMvc.perform(delete("/admin/delete/{username}", "tester_delete")
                        .with(csrf())
                        .with(httpBasic("tester", "test123")))
                        .andExpect(status().isOk());
    }

    // ADMIN 회원 강퇴 실패 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_delete_user_failure() throws Exception{
        when(userService.delete("tester_delete")).thenReturn(false);
        mockMvc.perform(delete("/admin/delete/{username}", "tester_delete")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }

    // ADMIN 회원 권한 부여 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_role_grant_success() throws Exception{
        when(userService.roleUpdate("tester_grant", "MANAGER", true)).thenReturn(true);
        mockMvc.perform(put("/admin/role_grant/{username}/{role}", "tester_grant", "MANAGER")
                        .with(csrf())
                        .with(httpBasic("tester", "test123")))
                        .andExpect(status().isOk());
    }

    // ADMIN 회원 권한 부여 예외 처리 테스팅
    @Test(expected = ServletException.class)
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_role_grant_exception() throws Exception{
        doThrow(new ServletException("Invalid Roles! Try Again!")).when(userService).roleUpdate("tester_grant", "INVALID", true);
        mockMvc.perform(put("/admin/role_grant/{username}/{role}", "tester_grant", "INVALID")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isInternalServerError());
    }

    // ADMIN 회원 권한 회수 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_role_revoke_success() throws Exception{
        when(userService.roleUpdate("tester_revoke", "MANAGER", false)).thenReturn(true);
        mockMvc.perform(put("/admin/role_revoke/{username}/{role}", "tester_grant", "MANAGER")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // ADMIN 회원 권한 회수 예외 처리 테스팅
    @Test(expected = ServletException.class)
    @WithMockUser(username="tester", password = "test123", roles = {"ADMIN"})
    public void admin_role_revoke_exception() throws Exception{
        doThrow(new ServletException("Invalid Roles! Try Again!")).when(userService).roleUpdate("tester_grant", "INVALID", false);
        mockMvc.perform(put("/admin/role_revoke/{username}/{role}", "tester_grant", "INVALID")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isInternalServerError());
    }
}
