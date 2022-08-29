package dev.jsedano.redis.movies.dao;

import com.google.gson.Gson;
import dev.jsedano.redis.movies.dto.MediaDTO;
import dev.jsedano.redis.movies.dto.MediaEntryDTO;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.search.*;

@Service
public class RedisDAO {

  @Autowired private JedisPooled jedisPooled;
  @Autowired private Gson gson;

  public void insert(String key, MediaDTO mediaDTO) {
    jedisPooled.jsonSet(key, gson.toJson(mediaDTO));
  }

  private MediaDTO getById(String id) {
    return jedisPooled.jsonGet(id, MediaDTO.class);
  }

  public List<MediaDTO> searchByTitle(String title) {
    return searchBy("titleIdx", "@title:(%s)", List.of(title));
  }

  public List<MediaDTO> searchByGenre(List<String> genre) {
    return searchBy("genreIdx", "@genres:{%s}", genre);
  }

  public List<MediaDTO> searchByCast(List<String> cast) {
    return searchBy("castIdx", "@cast:{%s}", cast);
  }

  public List<MediaDTO> searchByProvider(List<String> provider) {
    return searchBy("providerIdx", "@providers:{%s}", provider);
  }

  private List<MediaDTO> searchBy(String indexName, String queryFormat, List<String> list) {
    List<MediaDTO> result = new LinkedList<MediaDTO>();
    Set<String> cleanedUpGenre =
        list.stream()
            .map(g -> g.replaceAll("[^a-zA-Z0-9\\s]", ""))
            .filter(g -> !g.isBlank())
            .map(g -> String.format(queryFormat, g))
            .collect(Collectors.toSet());

    if (cleanedUpGenre.isEmpty()) {
      return result;
    }
    SearchResult searchResult =
        jedisPooled.ftSearch(indexName, new Query(String.join(" ", cleanedUpGenre)));

    for (Document d : searchResult.getDocuments()) {
      result.add(getById(d.getId()));
    }
    return result;
  }

  public MediaEntryDTO searchByExactTitle(String title, String cleanTitle) {
    SearchResult searchResult =
        jedisPooled.ftSearch(
            "titleIdx", new Query("@title:(" + cleanTitle + ")").returnFields("title"));
    for (Document d : searchResult.getDocuments()) {
      if (title.equals(d.get("title"))) {
        return new MediaEntryDTO(d.getId(), getById(d.getId()));
      }
    }
    return null;
  }
}
