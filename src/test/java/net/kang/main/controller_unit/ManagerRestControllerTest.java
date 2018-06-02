package net.kang.main.controller_unit;

import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.ManagerRestController;
import net.kang.main.domain.Role;
import net.kang.main.exception.MyAccessDeniedHandler;
import net.kang.main.service.UserService;
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

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// MANAGER 권한을 가진 회원을 위한 REST Controller를 확인하는 테스팅 클래스이다.
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@WebAppConfiguration
@SpringBootTest(classes = {net.kang.main.RestSpringSecurityApplication.class})
public class ManagerRestControllerTest {
    MockMvc mockMvc;

    // Security Configuration을 위해 이 요소들은 Mock으로 설정해둬야 한다.
    @Mock AuthProvider authProvider;
    @Mock AuthenticationEntryPoint authenticationEntryPoint;
    @Mock AuthLoginSuccessHandler authLoginSuccessHandler;
    @Mock MyAccessDeniedHandler myAccessDeniedHandler;

    // UserService는 별도로 Mock으로 설정한다.
    @Mock UserService userService;

    // ManagerRestController는 Mock으로 설정하지 않는다.
    @InjectMocks ManagerRestController adminRestController;

    @Autowired Filter springSecurityFilterChain;

    // 테스팅 전에 초기화하는 과정. 실행 결과는 항상 출력되도록 설정한다.
    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(adminRestController)
                .addFilters(springSecurityFilterChain)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();

        MockitoAnnotations.initMocks(this);
    }

    // MANAGER 권한을 가진 사람에 대해 접근 가능을 확인한다.
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"MANAGER"})
    public void manager_access_process_success() throws Exception{
        mockMvc.perform(get("/manager/login_process")
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // USER 권한을 가진 사람에 대해 403 에러를 발생하는지 확인한다.
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"USER"})
    public void manager_access_process_forbidden() throws Exception{
        mockMvc.perform(get("/manager/login_process")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isForbidden());
    }

    // 비회윈인 사람에 대해 401 에러를 발생하는지 확인한다.
    @Test
    @WithAnonymousUser
    public void manager_access_process_unauthorized() throws Exception{
        mockMvc.perform(get("/manager/login_process")
                .with(csrf()))
                .andExpect(status().isUnauthorized());
    }

    // MANAGER 권한 별 회원 수 파악 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"MANAGER"})
    public void manager_counting_with_role() throws Exception{
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("USER");
        Role role2 = new Role();
        role2.setId(2);
        role2.setName("MANAGER");

        Map<Role, Long> map = new HashMap<Role, Long>();
        map.put(role1, 30L);
        map.put(role2, 5L);

        when(userService.countWithManagerAndUser()).thenReturn(map);
        mockMvc.perform(get("/manager/count/role")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // MANAGER 회원 강퇴 성공 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"MANAGER"})
    public void admin_delete_user_success() throws Exception{
        when(userService.deleteForManager("tester_delete")).thenReturn(true);
        mockMvc.perform(delete("/manager/delete/{username}", "tester_delete")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    // MANAGER 회원 강퇴 실패 테스팅
    @Test
    @WithMockUser(username="tester", password = "test123", roles = {"MANAGER"})
    public void admin_delete_user_failure() throws Exception{
        when(userService.deleteForManager("tester_delete")).thenReturn(false);
        mockMvc.perform(delete("/manager/delete/{username}", "tester_delete")
                .with(csrf())
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }
}
