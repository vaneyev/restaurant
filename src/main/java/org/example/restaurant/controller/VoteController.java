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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.function.BiConsumer;

@RestController
@RequestMapping("/votes")
@Slf4j
@RequiredArgsConstructor
public class VoteController {
    private final static LocalTime limitTime = LocalTime.of(11, 0);

    private final VoteRepository voteRepository;

    private final DateTimeService dateTimeService;

    @GetMapping("/count/restaurants/{restaurantId}")
    public ResponseEntity<Long> getCount(
            @PathVariable Long restaurantId,
            @Param("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        log.info("Getting count of votes by restaurant id {} and date {}.", restaurantId, date);
        return ResponseEntity.ok(voteRepository.countByRestaurantIdAndDate(restaurantId, date));
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

    @PutMapping("/restaurants/{restaurantId}")
    @Transactional
    public ResponseEntity<?> vote(@AuthenticationPrincipal JpaUserDetails user, @PathVariable long restaurantId) {
        return getVoteResponseEntity(
                user,
                (oldVote, dateTime) -> {
                    oldVote.setRestaurantId(restaurantId);
                    voteRepository.save(oldVote);
                    log.info("Vote has been changed for user {}, restaurant id {}, date {}.", user.getUsername(), restaurantId, dateTime);
                },
                (vote, dateTime) -> {
                    vote.setRestaurantId(restaurantId);
                    voteRepository.save(vote);
                    log.info("Vote has been set for user {}, restaurant id {}, date {}.", user.getUsername(), restaurantId, dateTime);
                },
                "Vote has not been set because current time {} is after {}");
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<?> delete(@AuthenticationPrincipal JpaUserDetails user) {
        return getVoteResponseEntity(
                user,
                (oldVote, dateTime) -> {
                    voteRepository.delete(oldVote);
                    log.info("Vote has been removed for user {}, date {}.", user.getUsername(), dateTime);
                },
                (vote, dateTime) -> {
                },
                "Vote has not been removed because current time {} is after {}");
    }

    private ResponseEntity<?> getVoteResponseEntity(
            JpaUserDetails user,
            BiConsumer<Vote, LocalDateTime> ifPresent,
            BiConsumer<Vote, LocalDateTime> ifAbsent,
            String errorInfo
    ) {
        LocalDateTime dateTime = dateTimeService.getLocalDateTime();
        Vote vote = new Vote(user.getId(), dateTime.toLocalDate());
        Optional<Vote> oldVote = voteRepository.findOne(Example.of(vote));
        if (dateTime.toLocalTime().isBefore(limitTime)) {
            if (oldVote.isPresent()) {
                ifPresent.accept(oldVote.get(), dateTime);
            } else {
                ifAbsent.accept(vote, dateTime);
            }
            return ResponseEntity.noContent().build();
        }
        log.info(errorInfo, dateTime.toLocalTime(), limitTime);
        return ResponseEntity.badRequest().body("Too late.");
    }
}
