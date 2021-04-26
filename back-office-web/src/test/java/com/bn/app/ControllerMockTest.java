package com.bn.app;

import com.bn.redis.RateLimiter;
import com.bn.web.api.ApiRateLimitInterceptor;
import com.bn.web.authorization.UserAuthorizationInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@Import(TestBeans.class)
public abstract class ControllerMockTest {
    @MockBean
    private UserAuthorizationInterceptor userAuthorizationInterceptor;
    @MockBean
    private ApiRateLimitInterceptor apiRateLimitInterceptor;
    @MockBean
    private RateLimiter rateLimiter;

    @BeforeEach
    void setup() throws IOException {
        when(userAuthorizationInterceptor.preHandle(any(), any(), any())).thenReturn(Boolean.TRUE);
        when(apiRateLimitInterceptor.preHandle(any(), any(), any())).thenReturn(Boolean.TRUE);
        when(rateLimiter.trySetRate(any(), anyLong(), anyLong(), any())).thenReturn(Boolean.TRUE);
        when(rateLimiter.tryAcquire(any())).thenReturn(Boolean.TRUE);
    }
}
