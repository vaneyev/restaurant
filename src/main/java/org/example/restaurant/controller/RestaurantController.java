package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.RestaurantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(value = RestaurantController.REST_URL)
@Slf4j
@RequiredArgsConstructor
public class RestaurantController {
    public static final String REST_URL = "/restaurants";

    private final RestaurantRepository restaurantRepository;

    @GetMapping
    public List<Restaurant> getAll() {
        log.info("Getting the all restaurants");
        return restaurantRepository.findAll();
    }

    @GetMapping("/{id}")
    public Restaurant get(@PathVariable Long id) {
        log.info("Getting a restaurant with id {}.", id);
        return restaurantRepository.findById(id).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@RequestBody @Valid Restaurant restaurant) {
        if (restaurant.getId() != null) {
            throw new IllegalArgumentException("Restaurant id is not null");
        }
        Restaurant created = restaurantRepository.save(restaurant);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        log.info("The restaurant with id {} has been created.", created.getId());
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @RequestBody @Valid Restaurant restaurant) {
        if (id != restaurant.getId()) {
            throw new IllegalArgumentException("Restaurant id don't equals path variable");
        }
        restaurantRepository.save(restaurant);
        log.info("The restaurant with id {} has been created.", id);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        restaurantRepository.deleteById(id);
        log.info("The restaurant with id {} has been deleted.", id);
    }
}
