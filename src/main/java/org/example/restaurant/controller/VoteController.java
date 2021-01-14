package org.example.restaurant.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.restaurant.model.Vote;
import org.example.restaurant.model.security.JpaUserDetails;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.DateTimeService;
import org.springframework.data.domain.Example;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

@RestController
@RequestMapping(VoteController.REST_URL)
@Slf4j
@RequiredArgsConstructor
public class VoteController {
    public static final String REST_URL = "/votes";

    private static final LocalTime limitTime = LocalTime.of(11, 0);

    private final VoteRepository voteRepository;

    private final DateTimeService dateTimeService;

    @GetMapping("/count/restaurants/{restaurantId}")
    public Long getCount(
            @PathVariable Long restaurantId,
            @Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Getting count of votes by restaurant id {} and date {}.", restaurantId, date);
        return voteRepository.countByRestaurantIdAndDate(restaurantId, date);
    }

    @GetMapping
    public ResponseEntity<Vote> get(
            @AuthenticationPrincipal JpaUserDetails user,
            @Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Getting vote status by user {}, date {}.", user.getUsername(), date);
        return voteRepository.findOne(Example.of(new Vote(user.getId(), date)))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<?> create(@AuthenticationPrincipal JpaUserDetails user, @RequestBody long restaurantId) {
        LocalDateTime dateTime = dateTimeService.getLocalDateTime();
        Vote vote = new Vote(user.getId(), dateTime.toLocalDate());
        Optional<Vote> oldVote = voteRepository.findOne(Example.of(vote));
        if (oldVote.isEmpty()) {
            vote.setRestaurantId(restaurantId);
            Vote created = voteRepository.save(vote);
            log.info("Vote has been set for user {}, restaurant id {}, date {}.", user.getUsername(), restaurantId, dateTime);
            URI uriOfNewResource = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path(REST_URL + "/{id}")
                    .buildAndExpand(created.getId())
                    .toUri();
            return ResponseEntity.created(uriOfNewResource).body(created);
        } else {
            log.info("Vote is already created for user {}, restaurant id {}, date {}.", user.getUsername(), restaurantId, dateTime);
            return ResponseEntity.badRequest().body("Vote is already created.");
        }
    }

    @PutMapping("/restaurants/{restaurantId}")
    @Transactional
    public ResponseEntity<?> update(@AuthenticationPrincipal JpaUserDetails user, @PathVariable long restaurantId) {
        LocalDateTime dateTime = dateTimeService.getLocalDateTime();
        if (dateTime.toLocalTime().isAfter(limitTime)) {
            throw new IllegalArgumentException("Too late.");
        }
        Vote vote = new Vote(user.getId(), dateTime.toLocalDate());
        Optional<Vote> oldVote = voteRepository.findOne(Example.of(vote));
        if (oldVote.isEmpty()) {
            throw new IllegalArgumentException("Too late.");
        }
        oldVote.get().setRestaurantId(restaurantId);
        voteRepository.save(oldVote.get());
        log.info("Vote has been set for user {}, restaurant id {}, date {}.", user.getUsername(), restaurantId, dateTime);
        return ResponseEntity.noContent().build();
    }
}
