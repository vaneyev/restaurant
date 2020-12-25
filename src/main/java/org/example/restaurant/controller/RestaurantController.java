package org.example.restaurant.controller;

import org.example.restaurant.model.Restaurant;
import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.RestaurantRepository;
import org.example.restaurant.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll() {
        return ResponseEntity.ok(restaurantRepository.findAll());
    }
}
