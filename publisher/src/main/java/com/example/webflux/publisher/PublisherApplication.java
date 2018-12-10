package com.example.webflux.publisher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static org.springframework.web.reactive.function.server.ServerResponse.ok;

@SpringBootApplication
@EnableWebFlux
public class PublisherApplication implements WebFluxConfigurer {

    @Bean
    public RouterFunction<ServerResponse> helloRouter(HelloHandler helloHandler) {
        return RouterFunctions.route()
                .GET("/hello/list", helloHandler::list)
                .GET("/hello/one", helloHandler::one)
                .build();
    }

    @Component
    static class HelloHandler {
        private HelloRepository helloRepository;

        HelloHandler(HelloRepository helloRepository) {
            this.helloRepository = helloRepository;
        }

        Mono<ServerResponse> list(ServerRequest request) {
            Flux<String> flux = helloRepository.list();
            return ok().contentType(MediaType.TEXT_PLAIN)
                    .body(flux,String.class);
        }

        Mono<ServerResponse> one(ServerRequest request) {
            return ok().contentType(MediaType.TEXT_PLAIN)
                    .body(helloRepository.one(),String.class);
        }
    }

    @Component
    static class HelloRepository {
        Flux<String> list(){
            return Flux.range(0,10).map(String::valueOf);
        }

        Mono<String> one(){
            return Mono.just("1");
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PublisherApplication.class, args);
    }
}
