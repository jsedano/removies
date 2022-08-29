package dev.jsedano.redis.movies.request;

import lombok.Data;

import java.util.List;

@Data
public class ProviderRequest {
  private List<String> provider;
}
