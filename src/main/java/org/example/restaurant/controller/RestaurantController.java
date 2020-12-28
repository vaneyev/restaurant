package org.example.restaurant.controller;

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
public class RestaurantController {
    public static final String REST_URL = "/restaurants";

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> get(@PathVariable Long id) {
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
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<String> update(@RequestBody @Valid Restaurant restaurant) {
        if (restaurant.getId() == null) {
            return ResponseEntity.badRequest().body("Restaurant id must not be null.");
        }
        if (restaurantRepository.findById(restaurant.getId()).isEmpty()) {
            return getResponseEntity(restaurant.getId());
        }
        restaurantRepository.save(restaurant);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<String> delete(@PathVariable Long id) {
        if (restaurantRepository.findById(id).isEmpty()) {
            return getResponseEntity(id);
        }
        restaurantRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> getResponseEntity(Long id) {
        return ResponseEntity.badRequest().body(String.format("Restaurant with id %d not found.", id));
    }
}
