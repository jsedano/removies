package dev.jsedano.redis.movies.dto;

import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MediaDTO {
  private String type;
  private String title;
  private String director;
  private Set<String> cast;
  private String country;
  private String dateAdded;
  private int releaseYear;
  private String rating;
  private String duration;
  private Set<String> genre;
  private Set<String> description;
  private Set<String> provider;
}
