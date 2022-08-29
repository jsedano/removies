package dev.jsedano.redis.movies.request;

import lombok.Data;

import java.util.List;

@Data
public class GenreRequest {
    private List<String> genre;
}
