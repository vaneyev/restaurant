package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Menu;
import org.example.restaurant.repository.MenuRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.persistence.EntityExistsException;
import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(value = MenuController.REST_URL)
@Slf4j
@RequiredArgsConstructor
public class MenuController {
    public static final String REST_URL = "/menus";

    private final MenuRepository menuRepository;

    @GetMapping
    @Cacheable("menus")
    public List<Menu> getAllByDate(@Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Get menus by date {}", date);
        return menuRepository.findByDate(date);
    }

    @GetMapping("/{id}")
    public Menu get(@PathVariable Long id) {
        log.info("Get menu by id {}", id);
        return menuRepository.getById(id).orElseThrow();
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> create(@RequestBody @Valid Menu menu) {
        if (menu.getId() != null) {
            throw new IllegalArgumentException("Menu id is not null");
        }
        if (menu.getRestaurant().getId() == null) {
            throw new IllegalArgumentException("Restaurant id must not be null.");
        }
        menu.getMenuItems().forEach(dish -> {
            if (dish.getId() != null) {
                throw new IllegalArgumentException("Dish id is not null");
            }
        });
        Menu created = menuRepository.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        log.info("The menu with id {} has been created", created.getId());
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void update(@PathVariable long id, @RequestBody @Valid Menu menu) {
        if (id != menu.getId()) {
            throw new IllegalArgumentException("Menu id don't equals path variable");
        }
        menuRepository.save(menu);
        log.info("The menu with id {} has been updated", id);
    }

    @DeleteMapping("/{id}")
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable long id) {
        menuRepository.deleteById(id);
        log.info("The menu with id {} has been deleted.", id);
    }
}
