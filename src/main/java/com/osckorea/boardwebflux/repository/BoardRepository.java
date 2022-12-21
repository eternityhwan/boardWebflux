package com.osckorea.boardwebflux.repository;

import com.osckorea.boardwebflux.domain.BoardEntity;
import org.springframework.data.repository.CrudRepository;

public interface BoardRepository extends CrudRepository<BoardEntity, Long> {


}
