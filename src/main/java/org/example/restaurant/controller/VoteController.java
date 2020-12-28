package org.example.restaurant.controller;

import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.JpaUserDetails;
import org.springframework.data.domain.Example;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
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
    private final VoteRepository voteRepository;

    public VoteController(VoteRepository voteRepository) {
        this.voteRepository = voteRepository;
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
        return ResponseEntity.ok(voteRepository.findOne(Example.of(new Vote(restaurantId, user.getId(), date))).isPresent());
    }

    @PutMapping
    @Transactional
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void vote(@AuthenticationPrincipal JpaUserDetails user, @RequestBody Long restaurantId) {
        Vote vote = new Vote(restaurantId, user.getId(), LocalDate.now());
        Optional<Vote> oldVote = voteRepository.findOne(Example.of(vote));
        if (LocalTime.now().isBefore(LocalTime.of(11, 0))) {
            if (oldVote.isPresent()) {
                voteRepository.delete(oldVote.get());
            } else {
                voteRepository.save(vote);
            }
        }
    }
}
