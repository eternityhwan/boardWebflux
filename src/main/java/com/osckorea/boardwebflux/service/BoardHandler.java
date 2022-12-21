package com.osckorea.boardwebflux.service;


import com.osckorea.boardwebflux.domain.Board;
import com.osckorea.boardwebflux.domain.BoardEntity;
import com.osckorea.boardwebflux.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;
import static org.springframework.web.reactive.function.server.ServerResponse.noContent;
import static org.springframework.web.reactive.function.server.ServerResponse.notFound;

@RequiredArgsConstructor
@Slf4j
public class BoardHandler {

    private final BoardRepository boardRepository;

    // 게시물 작성
    public Mono<ServerResponse> save(ServerRequest request) {
        Mono<Board> boardMono = request.bodyToMono(Board.class);

        return boardMono.flatMap(board
                -> Mono.fromCallable(()
                -> boardRepository.save(BoardEntity.builder()
                .id(board.getId())
                .title(board.getTitle())
                .contents(board.getContents())
                .build())))
            .publishOn(Schedulers.boundedElastic())
            .flatMap(board -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).bodyValue(board));
    }

    // 게시물 리스트
    public Mono<ServerResponse> list(ServerRequest request) {
        Flux<BoardEntity>
            boardFlux = Flux.defer(() -> Flux.fromIterable(boardRepository.findAll())).subscribeOn(Schedulers.boundedElastic());

        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(boardFlux, BoardEntity.class);
    }

    // 게시물 상세보기
    public Mono<ServerResponse> show(ServerRequest request) {
        Long boardId = Long.valueOf(request.pathVariable("boardId"));
        return Mono.fromCallable(() -> this.boardRepository.findById(boardId).orElse(new BoardEntity()))
            .publishOn(Schedulers.boundedElastic())
            // fromValue (deprecated) > fromObject
            .flatMap(board -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON).body(fromValue(board)))
            .switchIfEmpty(notFound().build());
    }


    // 게시물 삭제
    public Mono<ServerResponse> delete(ServerRequest request) {
        Long boardId = Long.valueOf(request.pathVariable("boardId"));
        return this.boardRepository.findById(boardId)
            .map(
                board -> {
                    this.boardRepository.delete(board);
                    return noContent().build();
                }
            ).orElse(notFound().build());
    }


    // 게시물 수정
    public Mono<ServerResponse> update(ServerRequest request) {
        Long boardId = Long.valueOf(request.pathVariable("boardId"));

        return request.bodyToMono(BoardEntity.class)
            .flatMap(board
                -> Mono.fromCallable(()
                -> boardRepository.save(board)))
            .flatMap(modBoard -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(fromValue(modBoard)));
    }

}
