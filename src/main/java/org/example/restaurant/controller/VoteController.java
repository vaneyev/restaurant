package org.example.restaurant.controller;

import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private final VoteRepository voteRepository;

    public VoteController(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getAll() {
        return ResponseEntity.ok(voteRepository.findAll());
    }
}
