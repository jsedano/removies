package dev.jsedano.redis.movies.util;

import com.google.gson.Gson;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dev.jsedano.redis.movies.dto.MediaDTO;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import redis.clients.jedis.*;
import redis.clients.jedis.search.Document;
import redis.clients.jedis.search.Query;
import redis.clients.jedis.search.SearchResult;

public final class FillDatabaseUtil {

  private FillDatabaseUtil() {}

  public static int readWithOpenCSV(
      JedisPooled jedisPooled, Gson gson, String file, String service, int index) {
    int i = index;
    try (Reader reader = new FileReader(new ClassPathResource(file).getFile())) {
      try (CSVReader csvReader = new CSVReader(reader)) {
        csvReader.skip(1);
        String[] line;

        while ((line = csvReader.readNext()) != null) {
          MediaDTO mediaDTO = parse(line, service);
          String cleanTitle = mediaDTO.getTitle().replaceAll("[^a-zA-Z0-9\\s]", "");
          if (cleanTitle.isBlank()) {
            continue;
          }
          boolean exactMatch = false;
          SearchResult searchResult =
              jedisPooled.ftSearch(
                  "titleIdx", new Query("@title:(" + cleanTitle + ")").returnFields("title"));
          for (Document d : searchResult.getDocuments()) {
            if (mediaDTO.getTitle().equals(d.get("title"))) {
              MediaDTO foundMediaDTO = jedisPooled.jsonGet(d.getId(), MediaDTO.class);
              exactMatch = true;
              foundMediaDTO.getCast().addAll(mediaDTO.getCast());
              foundMediaDTO.getGenre().addAll(mediaDTO.getGenre());
              foundMediaDTO.getProvider().addAll(mediaDTO.getProvider());
              foundMediaDTO.getDescription().addAll(mediaDTO.getDescription());
              setNonBlank(mediaDTO::getCountry, foundMediaDTO::setCountry);
              setNonBlank(mediaDTO::getDirector, foundMediaDTO::setDirector);
              setNonBlank(mediaDTO::getRating, foundMediaDTO::setRating);
              setNonBlank(mediaDTO::getType, foundMediaDTO::setType);
              setNonBlank(mediaDTO::getDuration, foundMediaDTO::setDuration);
              setNonBlank(mediaDTO::getDateAdded, foundMediaDTO::setDateAdded);
              foundMediaDTO.setReleaseYear(
                  foundMediaDTO.getReleaseYear() == 0
                      ? mediaDTO.getReleaseYear()
                      : foundMediaDTO.getReleaseYear());

              jedisPooled.jsonSet(d.getId(), gson.toJson(foundMediaDTO));
            }
          }
          if (!exactMatch) {
            jedisPooled.jsonSet("media:" + i++, gson.toJson(mediaDTO));
          }
        }
      } catch (IOException | CsvValidationException e) {
        throw new RuntimeException(e);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    return i;
  }

  public static MediaDTO parse(String[] media, String service) {
    return MediaDTO.builder()
        .type(media[1])
        .title(media[2])
        .director(media[3])
        .cast(separateByCommaAndTrim(media[4]))
        .country(media[5])
        .dateAdded(media[6])
        .releaseYear(Objects.isNull(media[7]) ? Integer.parseInt(media[7]) : 0)
        .rating(media[8])
        .duration(media[9])
        .genre(separateByCommaAndTrim(media[10]))
        .description(Set.of(media[11]))
        .provider(Set.of(service))
        .build();
  }

  private static Set<String> separateByCommaAndTrim(String stringToSeparate) {
    return Arrays.asList(stringToSeparate.split(",")).stream()
        .map(String::trim)
        .filter(s -> !s.isBlank())
        .collect(Collectors.toSet());
  }

  private static void setNonBlank(Supplier<String> getter, Consumer<String> setter) {
    String s = getter.get();
    if (Objects.nonNull(s) && !s.isBlank()) {
      setter.accept(s);
    }
  }

  public static void main(String[] args) {
    JedisPooled jedisPooled = new JedisPooled("localhost", 6379);
    Gson gson = new Gson();
    int index = 0;
    index =
        FillDatabaseUtil.readWithOpenCSV(jedisPooled, gson, "netflix_titles.csv", "Netflix", index);
    index =
        FillDatabaseUtil.readWithOpenCSV(
            jedisPooled, gson, "amazon_prime_titles.csv", "Amazon Prime", index);
    index =
        FillDatabaseUtil.readWithOpenCSV(
            jedisPooled, gson, "disney_plus_titles.csv", "Disney Plus", index);
    FillDatabaseUtil.readWithOpenCSV(jedisPooled, gson, "hulu_titles.csv", "Hulu", index);
  }
}
