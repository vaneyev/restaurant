package org.example.restaurant.controller;

import org.example.restaurant.model.Menu;
import org.example.restaurant.repository.MenuRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuRepository menuRepository;

    public MenuController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @GetMapping("/restaurant/{id}/date/{date}")
    public ResponseEntity<List<Menu>> getByRestaurantId(@PathVariable Long id, @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(menuRepository.findByRestaurantIdAndDate(id, date));
    }
}
