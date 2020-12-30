package org.example.restaurant.controller;

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
public class MenuController {
    public static final String REST_URL = "/menus";

    private final MenuRepository menuRepository;

    private final SmartValidator validator;

    public MenuController(MenuRepository menuRepository, SmartValidator validator) {
        this.menuRepository = menuRepository;
        this.validator = validator;
    }

    @GetMapping("/dates/{date}")
    public ResponseEntity<List<Menu>> getAllByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(menuRepository.findByDate(date));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Menu> get(@PathVariable Long id) {
        return menuRepository.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @Transactional
    public ResponseEntity<?> create(@RequestBody @Valid Menu menu, BindingResult bindingResult) {
        menu.setId(null);
        if (menu.getRestaurant() == null || menu.getRestaurant().getId() == null) {
            return ResponseEntity.badRequest().body("Restaurant and its id must not be null.");
        }
        if (menuRepository.findFirstByRestaurantIdAndDate(menu.getRestaurant().getId(), menu.getDate()).isPresent()) {
            return ResponseEntity.badRequest().body("Menu with these restaurant and date is already created.");
        }
        menu.getDishes().forEach(dish -> {
            dish.setId(null);
            validator.validate(dish, bindingResult);
        });
        if (bindingResult.hasErrors()) {
            return getDishErrorResponseEntity(bindingResult);
        }
        Menu created = menuRepository.save(menu);
        URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(REST_URL + "/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(uriOfNewResource).body(created);
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> update(@RequestBody @Valid Menu menu, BindingResult bindingResult) {
        if (menu.getId() == null) {
            return ResponseEntity.badRequest().body("Menu id must not be null.");
        }
        Optional<Menu> oldMenu = menuRepository.findById(menu.getId());
        if (oldMenu.isEmpty()) {
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
            return getDishErrorResponseEntity(bindingResult);
        }
        menuRepository.save(menu);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @Transactional
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (menuRepository.findById(id).isEmpty()) {
            return getMenuErrorResponseEntity(id);
        }
        menuRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<String> getMenuErrorResponseEntity(Long id) {
        return ResponseEntity.badRequest().body(String.format("Menu with id %d not found.", id));
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
