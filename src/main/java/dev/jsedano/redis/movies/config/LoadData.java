package dev.jsedano.redis.movies.config;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import dev.jsedano.redis.movies.dao.RedisDAO;
import dev.jsedano.redis.movies.dto.MediaDTO;
import dev.jsedano.redis.movies.dto.MediaEntryDTO;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

@Configuration
public class LoadData {

  @Autowired private RedisDAO redisDAO;

  @PostConstruct
  public void init() {

    int index = 0;
    index = readWithOpenCSV("netflix_titles.csv", "Netflix", index);
    index = readWithOpenCSV("amazon_prime_titles.csv", "Amazon Prime", index);
    index = readWithOpenCSV("disney_plus_titles.csv", "Disney Plus", index);
    readWithOpenCSV("hulu_titles.csv", "Hulu", index);
  }

  private int readWithOpenCSV(String file, String service, int index) {
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

          MediaEntryDTO foundMediaEntryDTO =
              redisDAO.searchByExactTitle(mediaDTO.getTitle(), cleanTitle);
          if (Objects.nonNull(foundMediaEntryDTO)) {
            MediaDTO foundMediaDTO = foundMediaEntryDTO.getMediaDTO();
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
            redisDAO.insert(foundMediaEntryDTO.getKey(), foundMediaDTO);
          } else {
            redisDAO.insert("media:" + i++, mediaDTO);
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

  private static MediaDTO parse(String[] media, String service) {
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
}
