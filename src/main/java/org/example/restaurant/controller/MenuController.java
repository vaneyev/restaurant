package org.example.restaurant.controller;

import org.example.restaurant.model.Menu;
import org.example.restaurant.repository.MenuRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/menus")
public class MenuController {

    private final MenuRepository menuRepository;

    public MenuController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @GetMapping("/date/{date}")
    public ResponseEntity<List<Menu>> getByDate(@PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(menuRepository.findByDate(date));
    }

    @PostMapping
    @Transactional
    public ResponseEntity<Menu> create(@RequestBody Menu menu) {
        return ResponseEntity.ok(menuRepository.save(menu));
    }
}
