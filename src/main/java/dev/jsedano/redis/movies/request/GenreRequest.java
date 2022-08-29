package dev.jsedano.redis.movies.request;

import java.util.List;
import lombok.Data;

@Data
public class GenreRequest {
  private List<String> genre;
}
