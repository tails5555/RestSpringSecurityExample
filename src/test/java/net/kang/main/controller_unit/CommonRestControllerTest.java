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
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.servlet.Filter;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @InjectMocks GuestRestController guestRestController;
    @Mock UserService userService;

    @Autowired
    Filter springSecurityFilterChain;

    private String jsonStringFromObject(Object object) throws JsonProcessingException {
        ObjectMapper objectMapper = Jackson2ObjectMapperBuilder.json()
                .serializationInclusion(JsonInclude.Include.NON_NULL)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .modules(new JSR310Module())
                .build();
        return objectMapper.writeValueAsString(object);
    }

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

    @Test
    public void user_confirm_profile() throws Exception{
        mockMvc.perform(get("/common/profile"))
                .andExpect(status().isOk());
    }
}
