package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.RestaurantRepository;
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

    @PostMapping
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

    @PutMapping
    @Transactional
    public ResponseEntity<String> update(@RequestBody @Valid Restaurant restaurant) {
        if (restaurant.getId() == null) {
            log.info("The restaurant has not been updated because id must not be null.");
            return ResponseEntity.badRequest().body("Restaurant id must not be null.");
        }
        if (restaurantRepository.findById(restaurant.getId()).isEmpty()) {
            log.info("The restaurant with id {} is not found.", restaurant.getId());
            return getResponseEntity(restaurant.getId());
        }
        restaurantRepository.save(restaurant);
        log.info("The restaurant with id {} has been created.", restaurant.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (restaurantRepository.findById(id).isEmpty()) {
            log.info("The restaurant with id {} is not found.", id);
            return getResponseEntity(id);
        }
        restaurantRepository.deleteById(id);
        log.info("The restaurant with id {} has been deleted.", id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> getResponseEntity(Long id) {
        return ResponseEntity.badRequest().body(String.format("The restaurant with id %d not found.", id));
    }
}
