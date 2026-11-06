package com.sebastian.spring.mas.aliado.aliado;

import com.sebastian.spring.mas.aliado.aliado.config.FeatureFlags;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(FeatureFlags.class)
public class AliadooApplication {

    public static void main(String[] args) {
        SpringApplication.run(AliadooApplication.class, args);
    }

}
