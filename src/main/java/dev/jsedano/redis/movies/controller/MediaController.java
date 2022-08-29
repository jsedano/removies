package dev.jsedano.redis.movies.controller;

import dev.jsedano.redis.movies.dao.RedisDAO;
import dev.jsedano.redis.movies.dto.MediaDTO;
import dev.jsedano.redis.movies.request.CastRequest;
import dev.jsedano.redis.movies.request.GenreRequest;
import dev.jsedano.redis.movies.request.ProviderRequest;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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

  @RequestMapping(method = RequestMethod.GET, value = "/v1/media/cast/{cast}")
  public List<MediaDTO> getMoviesByCast(CastRequest castRequest) {
    return redisDAO.searchByCast(castRequest.getCastMember());
  }

  @RequestMapping(method = RequestMethod.GET, value = "/v1/media/provider/{provider}")
  public List<MediaDTO> getMoviesByProvider(ProviderRequest providerRequest) {
    return redisDAO.searchByProvider(providerRequest.getProvider());
  }
}
