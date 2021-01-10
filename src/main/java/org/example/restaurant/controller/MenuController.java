package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Dish;
import org.example.restaurant.model.Menu;
import org.example.restaurant.repository.MenuRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = MenuController.REST_URL)
@Slf4j
@RequiredArgsConstructor
public class MenuController {
    public static final String REST_URL = "/menus";

    private final MenuRepository menuRepository;

    private final SmartValidator validator;

    @GetMapping("/dates/{date}")
    @Cacheable("menus")
    public ResponseEntity<List<Menu>> getAllByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Get menus by date {}", date);
        return ResponseEntity.ok(menuRepository.findByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> get(@PathVariable Long id) {
        return menuRepository.getById(id)
                .map(menu -> {
                    log.info("Get menu by id {}", id);
                    return ResponseEntity.ok(menu);
                })
                .orElseGet(() -> {
                    log.info("Menu with id {} not found", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> create(@RequestBody @Valid Menu menu, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return getMenuErrorResponseEntity(bindingResult);
        }
        menu.setId(null);
        if (menu.getRestaurant().getId() == null) {
            log.info("Menu has not been created because restaurant id is null.");
            return ResponseEntity.badRequest().body("Restaurant id must not be null.");
        }
        if (menuRepository.findFirstByRestaurantIdAndDate(menu.getRestaurant().getId(), menu.getDate()).isPresent()) {
            log.info("The menu has not been created because the menu with these restaurant id {} and date {} is already created.",
                    menu.getRestaurant().getId(), menu.getDate());
            return ResponseEntity.badRequest().body("The menu with these restaurant and date is already created.");
        }
        menu.getDishes().forEach(dish -> {
            dish.setId(null);
            validator.validate(dish, bindingResult);
        });
        if (bindingResult.hasErrors()) {
            log.info("The menu has not been created because there are some errors in dishes.");
            return getDishErrorResponseEntity(bindingResult);
        }
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
    public ResponseEntity<?> update(@PathVariable long id, @RequestBody List<Dish> dishes, BindingResult bindingResult) {
        Optional<Menu> oldMenu = menuRepository.getById(id);
        if (oldMenu.isEmpty()) {
            return getMenuNotFoundResponseEntity(id);
        }
        Menu menu = new Menu(id, oldMenu.get().getRestaurant(), oldMenu.get().getDate());
        dishes.forEach(dish -> {
            dish.setMenu(menu);
            if (!oldMenu.get().getDishes().contains(dish)) {
                dish.setId(null);
            }
            validator.validate(dish, bindingResult);
        });
        if (bindingResult.hasErrors()) {
            log.info("The menu has not been updated because there are some errors in dishes");
            return getDishErrorResponseEntity(bindingResult);
        }
        menu.setDishes(dishes);
        menuRepository.save(menu);
        log.info("The menu with id {} has been updated", id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable long id) {
        if (menuRepository.findById(id).isEmpty()) {
            return getMenuNotFoundResponseEntity(id);
        }
        menuRepository.deleteById(id);
        log.info("The menu with id {} has been deleted.", id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> getMenuNotFoundResponseEntity(Long id) {
        log.info("The menu with id {} is not found.", id);
        return ResponseEntity.badRequest().body(String.format("Menu with id %d is not found.", id));
    }

    private ResponseEntity<String> getMenuErrorResponseEntity(BindingResult bindingResult) {
        return getErrorResponseEntity(bindingResult, "Menu data is not valid.\n");
    }

    private ResponseEntity<String> getDishErrorResponseEntity(BindingResult bindingResult) {
        return getErrorResponseEntity(bindingResult, "Dish data is not valid.\n");
    }

    private ResponseEntity<String> getErrorResponseEntity(BindingResult bindingResult, String message) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(message);
        bindingResult.getFieldErrors().forEach(fieldError -> {
            stringBuilder.append(fieldError.getField());
            stringBuilder.append(": ");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append("\n");
        });
        log.info(stringBuilder.toString());
        return ResponseEntity.badRequest().body(stringBuilder.toString());
    }
}
