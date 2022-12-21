package com.osckorea.boardwebflux.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Board {

    private Long id;
    private String title;
    private String contents;
}
