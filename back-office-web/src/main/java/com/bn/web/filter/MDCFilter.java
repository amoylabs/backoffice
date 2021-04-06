package com.bn.web.filter;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ReadListener;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
@Order(0)
@Slf4j
public class MDCFilter extends OncePerRequestFilter {
    private static final String REQUEST_ID = "requestId";
    private static final String UNKNOWN_IP_ADDRESS = "unknown";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        try {
            String requestId = Optional.ofNullable(request.getHeader("X-Request-Id")).orElseGet(() -> UUID.randomUUID().toString());
            MDC.put(REQUEST_ID, requestId);
            String clientIP = getClientIP(request);
            String queryString = request.getQueryString() != null ? "?" + request.getQueryString() : "";
            log.info("IP:{}, Method:{}, URI:{}{}", clientIP, request.getMethod(), request.getRequestURI(), queryString);
            if (HttpMethod.POST.name().equals(request.getMethod())) {
                MultiReadHttpServletRequest multiReadHttpServletRequest = new MultiReadHttpServletRequest(request);
                String bodyString = multiReadHttpServletRequest.getRequestBody();
                if (StrUtil.isNotBlank(bodyString)) {
                    log.info("Body:{}", bodyString);
                }
                chain.doFilter(multiReadHttpServletRequest, response);
            } else {
                chain.doFilter(request, response);
            }
        } finally {
            MDC.clear();
        }
    }

    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (isIPAddressInvalid(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isIPAddressInvalid(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isIPAddressInvalid(ip)) {
            ip = request.getRemoteAddr();
        }
        // use the first IP address if various
        final String[] ipList = ip.split(",");
        for (final String str : ipList) {
            if (!UNKNOWN_IP_ADDRESS.equalsIgnoreCase(str)) {
                ip = str;
                break;
            }
        }
        return ip;
    }

    private boolean isIPAddressInvalid(String ip) {
        return StrUtil.isBlank(ip) || UNKNOWN_IP_ADDRESS.equalsIgnoreCase(ip);
    }

    private static class MultiReadHttpServletRequest extends HttpServletRequestWrapper {
        private String requestBody;

        MultiReadHttpServletRequest(HttpServletRequest request) {
            super(request);
            requestBody = "";
            try {
                StringBuilder stringBuilder = new StringBuilder();
                InputStream inputStream = request.getInputStream();
                byte[] bs = new byte[1024];
                int len = inputStream.read(bs);
                while (len != -1) {
                    stringBuilder.append(new String(bs, 0, len, StandardCharsets.UTF_8));
                    len = inputStream.read(bs);
                }
                requestBody = stringBuilder.toString();
            } catch (IOException ex) {
                log.error("Failed to get request body", ex);
            }
        }

        @Override
        public ServletInputStream getInputStream() {
            return new RequestBodyServletInputStreams(requestBody.getBytes(StandardCharsets.UTF_8));
        }

        @Override
        public BufferedReader getReader() {
            return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
        }

        String getRequestBody() {
            return requestBody.replaceAll("\n", "");
        }
    }

    private static class RequestBodyServletInputStreams extends ServletInputStream {
        private final InputStream requestBodyInputStream;

        RequestBodyServletInputStreams(byte[] requestBody) {
            this.requestBodyInputStream = new ByteArrayInputStream(requestBody);
        }

        @Override
        public int read() throws IOException {
            return requestBodyInputStream.read();
        }

        @SneakyThrows
        @Override
        public boolean isFinished() {
            return requestBodyInputStream.available() == 0;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener readListener) {
        }
    }
}
