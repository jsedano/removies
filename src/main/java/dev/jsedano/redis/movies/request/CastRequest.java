package dev.jsedano.redis.movies.request;

import java.util.List;
import lombok.Data;

@Data
public class CastRequest {
  private List<String> castMember;
}
