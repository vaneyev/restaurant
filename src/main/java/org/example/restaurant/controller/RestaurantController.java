package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.RestaurantRepository;
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
    public ResponseEntity<List<Restaurant>> getAll() {
        log.info("Getting the all restaurants");
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable Long id) {
        log.info("Getting a restaurant with id {}.", id);
        return restaurantRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Restaurant> create(@RequestBody @Valid Restaurant restaurant) {
        restaurant.setId(null);
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
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody @Valid Restaurant restaurant) {
        return getResponseEntity(id, () -> {
            restaurant.setId(id);
            restaurantRepository.save(restaurant);
            log.info("The restaurant with id {} has been created.", id);
        });
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> delete(@PathVariable long id) {
        return getResponseEntity(id, () -> {
            restaurantRepository.deleteById(id);
            log.info("The restaurant with id {} has been deleted.", id);
        });
    }

    private ResponseEntity<String> getResponseEntity(long id, Runnable runnable) {
        if (restaurantRepository.findById(id).isEmpty()) {
            log.info("The restaurant with id {} is not found.", id);
            return ResponseEntity.badRequest().body(String.format("The restaurant with id %d is not found.", id));
        }
        runnable.run();
        return ResponseEntity.noContent().build();
    }
}
