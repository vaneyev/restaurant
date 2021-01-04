package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Menu;
import org.example.restaurant.repository.MenuRepository;
import org.springframework.format.annotation.DateTimeFormat;
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

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody @Valid Menu menu, BindingResult bindingResult) {
        menu.setId(null);
        if (menu.getRestaurant() == null || menu.getRestaurant().getId() == null) {
            log.info("Menu has not been created because restaurant or its id is null.");
            return ResponseEntity.badRequest().body("Restaurant and its id must not be null.");
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

    @PutMapping
    @Transactional
    public ResponseEntity<?> update(@RequestBody @Valid Menu menu, BindingResult bindingResult) {
        if (menu.getId() == null) {
            log.info("The menu has not been updated because menu id must not be null.");
            return ResponseEntity.badRequest().body("Menu id must not be null.");
        }
        Optional<Menu> oldMenu = menuRepository.getById(menu.getId());
        if (oldMenu.isEmpty()) {
            log.info("The menu with id {} is not found.", menu.getId());
            return getMenuErrorResponseEntity(menu.getId());
        }
        menu.setRestaurant(oldMenu.get().getRestaurant());
        menu.getDishes().forEach(dish -> {
            if (!oldMenu.get().getDishes().contains(dish)) {
                dish.setId(null);
                validator.validate(dish, bindingResult);
            }
        });
        if (bindingResult.hasErrors()) {
            log.info("The menu has not been updated because there are some errors in dishes");
            return getDishErrorResponseEntity(bindingResult);
        }
        menuRepository.save(menu);
        log.info("The menu with id {} has been updated", menu.getId());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (menuRepository.findById(id).isEmpty()) {
            log.info("The menu with id {} is not found.", id);
            return getMenuErrorResponseEntity(id);
        }
        menuRepository.deleteById(id);
        log.info("The menu with id {} has been deleted.", id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> getMenuErrorResponseEntity(Long id) {
        return ResponseEntity.badRequest().body(String.format("Menu with id %d is not found.", id));
    }

    private ResponseEntity<String> getDishErrorResponseEntity(BindingResult bindingResult) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Dish data is not valid.\n");
        bindingResult.getFieldErrors().forEach(fieldError -> {
            stringBuilder.append(fieldError.getField());
            stringBuilder.append(": ");
            stringBuilder.append(fieldError.getDefaultMessage());
            stringBuilder.append("\n");
        });
        return ResponseEntity.badRequest().body(stringBuilder.toString());
    }
}
