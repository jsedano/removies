package dev.jsedano.redis.movies.request;

import lombok.Data;

import java.util.List;

@Data
public class CastRequest {
  private List<String> castMember;
}
