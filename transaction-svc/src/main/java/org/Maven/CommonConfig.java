package org.Maven;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.json.simple.parser.JSONParser;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
public class CommonConfig {
    @Bean
    ObjectMapper getObjectMapper(){
        return new ObjectMapper();
    }

    @Bean
    JSONParser getJsonParser(){
        return new JSONParser();
    }

    @LoadBalanced
    @Bean
    RestTemplate getRestTemplate(){
        return new RestTemplate();
    }

    @Bean
    CircuitBreakerConfig getCircuitBreaker(){

        return CircuitBreakerConfig.custom()
                .failureRateThreshold(20)
                .slidingWindowSize(5)
                .permittedNumberOfCallsInHalfOpenState(4)
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .minimumNumberOfCalls(5)
                .enableAutomaticTransitionFromOpenToHalfOpen()
                .maxWaitDurationInHalfOpenState(Duration.of(5, ChronoUnit.SECONDS))
                .build();
    }
}
