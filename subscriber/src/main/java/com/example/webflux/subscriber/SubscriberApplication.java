package com.example.webflux.subscriber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;

@SpringBootApplication
@EnableWebFlux
public class SubscriberApplication {
	@Bean
	public RouterFunction<ServerResponse> helloRouter(ClientHandler clientHandler) {
		return RouterFunctions.route()
				.GET("/client", clientHandler::get)
				.build();
	}

	@Component
	static class ClientHandler {
		final List<String> validTypes = List.of("list", "one");

		Mono<ServerResponse> get(ServerRequest request) {
			String type = request.queryParam("type").filter(validTypes::contains).orElseThrow(IllegalArgumentException::new);
			return client(type).flatMap(r -> ServerResponse.status(r.statusCode())
					.body(r.bodyToMono(String.class).map(clientMsg()), String.class));
		}

		private Function<String, String> clientMsg() {
			return s->"client[" + s + "]";
		}

		private Mono<ClientResponse> client(String path) {
			return WebClient.create("http://localhost:8080/hello/" + path).get()
					.acceptCharset(StandardCharsets.UTF_8)
					.accept(MediaType.APPLICATION_JSON_UTF8)
					.exchange();
		}
	}
	public static void main(String[] args) {
		SpringApplication.run(SubscriberApplication.class, args);
	}
}
