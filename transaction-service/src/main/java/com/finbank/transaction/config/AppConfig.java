package com.finbank.transaction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    /**
     * @LoadBalanced cho phep goi "http://account-service/..." thay vi host:port cu the.
     * Spring Cloud LoadBalancer se hoi Eureka de lay danh sach instance cua account-service
     * va chon mot instance cho moi request.
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate(RestTemplateBuilder builder,
                                     @Value("${account-service.connect-timeout:3s}") Duration connectTimeout,
                                     @Value("${account-service.read-timeout:5s}") Duration readTimeout) {
        // Dat timeout de request khong bi treo vo han khi Account Service khong phan hoi
        return builder
                .setConnectTimeout(connectTimeout)
                .setReadTimeout(readTimeout)
                .build();
    }
}
