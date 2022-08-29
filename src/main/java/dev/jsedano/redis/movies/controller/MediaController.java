package dev.jsedano.redis.movies.controller;

import dev.jsedano.redis.movies.dao.RedisDAO;
import dev.jsedano.redis.movies.request.GenreRequest;
import dev.jsedano.redis.movies.dto.MediaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MediaController {

  @Autowired private RedisDAO redisDAO;
  
  @RequestMapping(method = RequestMethod.GET, value = "/v1/media/title/{title}")
  public List<MediaDTO> getMoviesByTitle(String title) {
    return redisDAO.searchByTitle(title);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/v1/media/genre/{genre}")
  public List<MediaDTO> getMoviesByGenre(GenreRequest genreRequest) {
    return redisDAO.searchByGenre(genreRequest.getGenre());
  }
}
