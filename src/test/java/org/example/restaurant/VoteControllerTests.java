package org.example.restaurant;

import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.DateTimeService;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTests extends AbstractControllerTests {
    private final Vote vote2 = new Vote(2L, 2L, 2L, early.toLocalDate());
    private final Vote addedVote = new Vote(3L, 2L, 1L, early.plusDays(1).toLocalDate());
    private final Vote changedVote = new Vote(2L, 2L, 1L, early.toLocalDate());

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DateTimeService dateTimeService;

    @Test
    void add() throws Exception {
        dateTimeService.setCustom(early.plusDays(1));
        mockMvc.perform(
                put("/votes/restaurants/{restaurantId}", 1L)
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, addedVote.getDate());
        Optional<Vote> actual = voteRepository.findOne(Example.of(vote));
        assertTrue(actual.isPresent());
        assertTrue(new ReflectionEquals(addedVote).matches(actual.get()));
    }

    @Test
    void change() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(
                put("/votes/restaurants/{restaurantId}", 1L)
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, early.toLocalDate());
        Optional<Vote> actual = voteRepository.findOne(Example.of(vote));
        assertTrue(actual.isPresent());
        assertTrue(new ReflectionEquals(changedVote).matches(actual.get()));
    }

    @Test
    void late() throws Exception {
        dateTimeService.setCustom(late);
        mockMvc.perform(
                put("/votes/restaurants/{restaurantId}", 1L)
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dateTimeService.setSystem();
    }

    @Test
    void remove() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(
                delete("/votes")
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, early.toLocalDate());
        Optional<Vote> removedVote = voteRepository.findOne(Example.of(vote));
        assertFalse(removedVote.isPresent());
    }

    @Test
    void lateRemove() throws Exception {
        dateTimeService.setCustom(late);
        mockMvc.perform(
                delete("/votes")
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dateTimeService.setSystem();
    }

    @Test
    void getPresent() throws Exception {
        String result = mockMvc.perform(get("/votes/dates/{date}", early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Vote actual = mapper.readValue(result, Vote.class);
        assertTrue(new ReflectionEquals(vote2).matches(actual));
    }

    @Test
    void getAbsent() throws Exception {
        mockMvc.perform(get("/votes/dates/{date}", early.plusDays(1).toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getCount() throws Exception {
        mockMvc.perform(get("/votes/count/restaurants/{restaurant}/dates/{date}", 1L, early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
