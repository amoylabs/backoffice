package com.bn.app;

import com.bn.web.authorization.UserAuthorizationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public abstract class ControllerMockTest {
    @MockBean
    private UserAuthorizationInterceptor userAuthorizationInterceptor;

    @BeforeEach
    void setup() throws IOException {
        when(userAuthorizationInterceptor.preHandle(any(), any(), any())).thenReturn(Boolean.TRUE);
    }
}
