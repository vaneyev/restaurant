package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.DateTimeService;
import org.example.restaurant.service.JpaUserDetails;
import org.springframework.data.domain.Example;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping("/votes")
@Slf4j
@RequiredArgsConstructor
public class VoteController {
    private final static LocalTime limitTime = LocalTime.of(11, 0);

    private final VoteRepository voteRepository;

    private final DateTimeService dateTimeService;

    @GetMapping("/count/restaurants/{restaurantId}/dates/{date}")
    public ResponseEntity<Long> getCount(
            @PathVariable Long restaurantId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(voteRepository.countByRestaurantIdAndDate(restaurantId, date));
    }

    @GetMapping("/restaurants/{restaurantId}/dates/{date}")
    public ResponseEntity<Boolean> get(
            @AuthenticationPrincipal JpaUserDetails user,
            @PathVariable Long restaurantId,
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(voteRepository.findOne(Example.of(new Vote(user.getId(), restaurantId, date))).isPresent());
    }

    @PutMapping
    @Transactional
    public ResponseEntity<?> vote(@AuthenticationPrincipal JpaUserDetails user, @RequestBody Long restaurantId) {
        LocalDateTime dateTime = dateTimeService.getLocalDateTime();
        Vote vote = new Vote(user.getId(), restaurantId, dateTime.toLocalDate());
        Optional<Vote> oldVote = voteRepository.findOne(Example.of(vote));
        if (dateTime.toLocalTime().isBefore(limitTime)) {
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
