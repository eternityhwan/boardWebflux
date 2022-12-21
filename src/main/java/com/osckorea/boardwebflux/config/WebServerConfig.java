package com.osckorea.boardwebflux.config;

import com.osckorea.boardwebflux.repository.BoardRepository;
import com.osckorea.boardwebflux.service.BoardHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class WebServerConfig {

    @Bean
    BoardHandler boardHandler(BoardRepository boardRepository) {
        return new BoardHandler(boardRepository);
    }

    @Bean
    RouterFunction<ServerResponse> routerFunction(BoardHandler boardHandler) {
        return RouterFunctions.route()
            .GET("/boards",accept(APPLICATION_JSON),boardHandler::list)
            .GET("/boards/{boardId}",accept(APPLICATION_JSON),boardHandler::show)
            .POST("/boards", boardHandler::save)
            .DELETE("/boards/{boardId}",boardHandler::delete)
            .PUT("/boards/{boardId}",accept(APPLICATION_JSON),boardHandler::update)
            .build();
    }
}
