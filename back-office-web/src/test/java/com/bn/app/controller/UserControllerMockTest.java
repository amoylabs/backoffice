package com.bn.app.controller;

import com.bn.authorization.UserAuthorizationInterceptor;
import com.bn.controller.UserController;
import com.bn.controller.request.CreateUserRequest;
import com.bn.domain.User;
import com.bn.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerMockTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserAuthorizationInterceptor userAuthorizationInterceptor;
    @MockBean
    private UserService userService;

    @BeforeEach
    void setup() throws IOException {
        when(userAuthorizationInterceptor.preHandle(any(), any(), any())).thenReturn(Boolean.TRUE);
    }

    @Test
    public void createUser() throws Exception {
        Long result = 1L;
        when(userService.create(any(User.class), anyString())).thenReturn(result);
        CreateUserRequest request = CreateUserRequest.builder()
            .name("test")
            .mobilePhone("12333222332")
            .password("q1w2e3r4")
            .build();
        MockHttpServletRequestBuilder builder = post("/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));
        ResultMatcher success = status().isCreated();
        ResultMatcher body = content().string(result.toString());
        mockMvc.perform(builder).andDo(print()).andExpect(success).andExpect(body);
    }

    @Test
    public void createUserWithoutPassword() throws Exception {
        CreateUserRequest request = CreateUserRequest.builder().mobilePhone("12333222332").name("").build();
        MockHttpServletRequestBuilder builder = post("/v1/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request));
        ResultMatcher badRequest = status().isBadRequest();
        mockMvc.perform(builder).andDo(print()).andExpect(badRequest);
    }
}
