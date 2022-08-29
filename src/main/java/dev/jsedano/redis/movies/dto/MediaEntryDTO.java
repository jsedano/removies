package dev.jsedano.redis.movies.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MediaEntryDTO {
  private String key;
  private MediaDTO mediaDTO;
}
