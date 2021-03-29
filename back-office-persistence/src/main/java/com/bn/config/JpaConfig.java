package com.bn.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EntityScan("com.bn.domain")
@EnableJpaRepositories("com.bn.repository")
public class JpaConfig {

}
