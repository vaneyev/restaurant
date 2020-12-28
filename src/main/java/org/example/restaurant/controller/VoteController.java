package org.example.restaurant.controller;

import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.ClockService;
import org.example.restaurant.service.JpaUserDetails;
import org.springframework.data.domain.Example;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/votes")
public class VoteController {
    private final static LocalTime limitTime = LocalTime.of(11, 0);

    private final VoteRepository voteRepository;

    private final ClockService clockService;

    public VoteController(VoteRepository voteRepository, ClockService clockService) {
        this.voteRepository = voteRepository;
        this.clockService = clockService;
    }

    @GetMapping
    public ResponseEntity<List<Vote>> getAll() {
        return ResponseEntity.ok(voteRepository.findAll());
    }

    @GetMapping("/restaurant/{restaurantId}/date/{date}")
    public ResponseEntity<Boolean> get(
            @AuthenticationPrincipal JpaUserDetails user,
            @PathVariable Long restaurantId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(voteRepository.findOne(Example.of(new Vote(user.getId(), restaurantId, date))).isPresent());
    }

    @PutMapping
    @Transactional
    public ResponseEntity<Object> vote(@AuthenticationPrincipal JpaUserDetails user, @RequestBody Long restaurantId) {
        Vote vote = new Vote(user.getId(), restaurantId, LocalDate.now(clockService.getClock()));
        Optional<Vote> oldVote = voteRepository.findOne(Example.of(vote));
        if (LocalTime.now(clockService.getClock()).isBefore(limitTime)) {
            if (oldVote.isPresent()) {
                voteRepository.delete(oldVote.get());
            } else {
                voteRepository.save(vote);
            }
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().body("Too late.");
    }
}
