package net.kang.main.controller_unit;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JSR310Module;
import net.kang.main.component.AuthLoginSuccessHandler;
import net.kang.main.component.AuthProvider;
import net.kang.main.component.AuthenticationEntryPoint;
import net.kang.main.controller.CommonRestController;
import net.kang.main.domain.Role;
import net.kang.main.exception.MyAccessDeniedHandler;
import net.kang.main.model.DetailVO;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.Filter;
import javax.servlet.ServletException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {net.kang.main.config.SecurityConfig.class})
@WebAppConfiguration
@SpringBootTest
public class CommonRestControllerTest {
    MockMvc mockMvc;

    @MockBean AuthProvider authProvider;
    @MockBean AuthenticationEntryPoint authenticationEntryPoint;
    @MockBean AuthLoginSuccessHandler authLoginSuccessHandler;
    @MockBean MyAccessDeniedHandler myAccessDeniedHandler;

    @InjectMocks CommonRestController commonRestController;
    @Mock UserService userService;
    @Mock Principal principal;

    @Autowired Filter springSecurityFilterChain;

    private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JSR310Module())
                .build();
        return objectMapper.writeValueAsString(object);
    }

    private UserVO createUserVO(String username, String name, String email, LocalDateTime birthday, String address, List<Role> roles){
        UserVO userVO = new UserVO(username, name, email, birthday, address, roles);
        return userVO;
    }

    private DetailVO createDetailVO(String beforePassword, String newPassword, String address, String email, LocalDateTime birthday){
        DetailVO detailVO = new DetailVO(beforePassword, newPassword, address, email, birthday);
        return detailVO;
    }

    @Before
    public void setUp() throws Exception{
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(commonRestController)
                .addFilters(springSecurityFilterChain)
                .alwaysDo(print())
                .apply(SecurityMockMvcConfigurers.springSecurity(springSecurityFilterChain))
                .build();

        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void user_confirm_profile_success() throws Exception{
        Role role = new Role();
        role.setId(1);
        role.setName("USER");

        UserVO userVO = this.createUserVO("tester", "tester", "tester@test.com", LocalDateTime.now(), "seoul", Arrays.asList(role));

        final String userName = "tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.findByUsername(userName)).thenReturn(userVO);

        mockMvc.perform(get("/common/profile")
                        .with(user("tester").password("test123").authorities(new SimpleGrantedAuthority("ROLE_USER")))
                        .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="tester", password = "test123")
    public void user_confirm_profile_failure() throws Exception{
        final String userName = "tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.findByUsername(userName)).thenReturn(null);
        mockMvc.perform(get("/common/profile")
                .with(user("tester"))
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles="MANAGER")
    public void user_update_success() throws Exception{
        DetailVO detailVO = this.createDetailVO("test123", "test234", "suwon", "tester@test.com", LocalDateTime.now());
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.update(userName, detailVO)).thenReturn(true);

        mockMvc.perform(put("/common/update")
                        .contentType(MediaType.APPLICATION_JSON_UTF8)
                        .content(jsonStringFromObject(detailVO))
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles="MANAGER")
    public void user_update_failure() throws Exception{
        DetailVO detailVO = this.createDetailVO("test123", "test234", "suwon", "tester@test.com", LocalDateTime.now());
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.update(userName, detailVO)).thenReturn(false);

        mockMvc.perform(put("/common/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonStringFromObject(detailVO))
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }

    @Test(expected = ServletException.class)
    @WithMockUser(username="tester", password = "test123", roles="MANAGER")
    public void user_update_exception() throws Exception{
        DetailVO detailVO = this.createDetailVO("test1", "test234", "suwon", "tester@test.com", LocalDateTime.now());
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        doThrow(new ServletException("Before Password Is Wrong.")).when(userService).update(userName, detailVO);

        mockMvc.perform(put("/common/update")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(jsonStringFromObject(detailVO))
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles="ADMIN")
    public void user_delete_success() throws Exception{
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.delete(userName)).thenReturn(true);

        mockMvc.perform(delete("/common/delete")
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles="ADMIN")
    public void user_delete_failure() throws Exception{
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.delete(userName)).thenReturn(false);

        mockMvc.perform(delete("/common/delete")
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles="USER")
    public void user_same_list_success() throws Exception{
        Role role1 = new Role();
        role1.setId(1);
        role1.setName("USER");
        Role role2 = new Role();
        role2.setId(2);
        role2.setName("MANAGER");
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.findForSameLayers(userName)).thenReturn(Arrays.asList(this.createUserVO("tester1", "tester1", "tester1@test.com", LocalDateTime.now(), "seoul", Arrays.asList(role1)), this.createUserVO("tester2", "tester2", "tester2@test.com", LocalDateTime.now(), "seongnam", Arrays.asList(role1, role2))));

        mockMvc.perform(get("/common/sameList")
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="tester", password = "test123", roles="MANAGER")
    public void user_same_list_failure() throws Exception{
        final String userName="tester";
        when(principal.getName()).thenReturn(userName);
        when(userService.findForSameLayers(userName)).thenReturn(new ArrayList<UserVO>());

        mockMvc.perform(get("/common/sameList")
                .with(httpBasic("tester", "test123")))
                .andExpect(status().isNotFound());
    }
}
