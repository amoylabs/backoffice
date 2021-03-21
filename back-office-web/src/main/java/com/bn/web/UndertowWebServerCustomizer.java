package com.bn.web;

import io.undertow.UndertowOptions;
import io.undertow.server.DefaultByteBufferPool;
import io.undertow.server.handlers.accesslog.AccessLogHandler;
import io.undertow.websockets.jsr.WebSocketDeploymentInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@Slf4j
public class UndertowWebServerCustomizer implements WebServerFactoryCustomizer<UndertowServletWebServerFactory> {
    private final ServerProperties serverProperties;

    public UndertowWebServerCustomizer(ServerProperties serverProperties) {
        this.serverProperties = serverProperties;
    }

    @Override
    public void customize(UndertowServletWebServerFactory factory) {
        log.info("Customize undertow web server ...");

        String pattern = serverProperties.getUndertow().getAccesslog().getPattern();
        // Record request timing only if the pattern matches
        if (logRequestProcessingTiming(pattern)) {
            factory.addBuilderCustomizers(builder -> builder.setServerOption(UndertowOptions.RECORD_REQUEST_START_TIME, Boolean.TRUE));
        }

        factory.addDeploymentInfoCustomizers(deploymentInfo -> deploymentInfo.addInitialHandlerChainWrapper(handler -> {
            Slf4jAccessLogReceiver accessLogReceiver = new Slf4jAccessLogReceiver();
            return new AccessLogHandler(handler, accessLogReceiver, pattern, Thread.currentThread().getContextClassLoader());
        }));

        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            WebSocketDeploymentInfo webSocketDeploymentInfo = new WebSocketDeploymentInfo();
            webSocketDeploymentInfo.setBuffers(new DefaultByteBufferPool(false, 1024));
            deploymentInfo.addServletContextAttribute("io.undertow.websockets.jsr.WebSocketDeploymentInfo", webSocketDeploymentInfo);
        });

        int cpuCoresNum = Runtime.getRuntime().availableProcessors();
        factory.setIoThreads(cpuCoresNum);
        factory.setWorkerThreads(cpuCoresNum * 8);
    }

    private boolean logRequestProcessingTiming(String pattern) {
        if (!StringUtils.hasText(pattern)) return false;
        return pattern.contains("%D") || pattern.contains("%T");
    }
}
