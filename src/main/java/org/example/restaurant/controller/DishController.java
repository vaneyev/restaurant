package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Dish;
import org.example.restaurant.repository.DishRepository;
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
@RequestMapping(value = DishController.REST_URL)
@Slf4j
@RequiredArgsConstructor
public class DishController {
    public static final String REST_URL = "/dishes";

    private final DishRepository dishRepository;

    @GetMapping
    public List<Dish> getAll() {
        log.info("Getting the all dishes");
        return dishRepository.findAll();
    }

    @GetMapping("/{id}")
    public Dish get(@PathVariable Long id) {
        log.info("Getting a dish with id {}.", id);
        return dishRepository.findById(id).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Dish> create(@RequestBody @Valid Dish dish) {
        if (dish.getId() != null) {
            throw new IllegalArgumentException("Dish id is not null");
        }
        Dish created = dishRepository.save(dish);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        log.info("The dish with id {} has been created.", created.getId());
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @RequestBody @Valid Dish dish) {
        if (id != dish.getId()) {
            throw new IllegalArgumentException("Dish id don't equals path variable");
        }
        dishRepository.save(dish);
        log.info("The dish with id {} has been created.", id);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        dishRepository.deleteById(id);
        log.info("The dish with id {} has been deleted.", id);
    }
}
