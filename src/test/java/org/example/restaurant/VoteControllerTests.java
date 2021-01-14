package org.example.restaurant;

import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.DateTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTests extends AbstractControllerTests {
    private final Vote vote2 = new Vote(2L, 2L, 2L, early.toLocalDate());
    private final Vote addedVote = new Vote(3L, 2L, 1L, early.plusDays(1).toLocalDate());
    private final Vote changedVote = new Vote(2L, 2L, 2L, early.toLocalDate());

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DateTimeService dateTimeService;

    @Test
    void add() throws Exception {
        dateTimeService.setCustom(early.plusDays(1));
        mockMvc.perform(
                post("/votes")
                        .with(userAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(1L)))
                .andDo(print())
                .andExpect(status().isCreated());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, addedVote.getDate());
        Optional<Vote> actual = voteRepository.findOne(Example.of(vote));
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(addedVote);
    }

    @Test
    void addAfter() throws Exception {
        dateTimeService.setCustom(late.plusDays(1));
        mockMvc.perform(
                post("/votes")
                        .with(userAuth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(1L)))
                .andDo(print())
                .andExpect(status().isCreated());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, addedVote.getDate());
        Optional<Vote> actual = voteRepository.findOne(Example.of(vote));
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(addedVote);
    }

    @Test
    void change() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(
                put("/votes/restaurants/{restaurantId}", 2L)
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, early.toLocalDate());
        Optional<Vote> actual = voteRepository.findOne(Example.of(vote));
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(changedVote);
    }

    @Test
    void changeAfter() throws Exception {
        dateTimeService.setCustom(late);
        mockMvc.perform(
                put("/votes/restaurants/{restaurantId}", 1L)
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dateTimeService.setSystem();
    }

    @Test
    void getPresent() throws Exception {
        String result = mockMvc.perform(get("/votes?date={date}", early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Vote actual = mapper.readValue(result, Vote.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(vote2);
    }

    @Test
    void getAbsent() throws Exception {
        mockMvc.perform(get("/votes?date={date}", early.plusDays(1).toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getCount() throws Exception {
        mockMvc.perform(get("/votes/count/restaurants/{restaurant}?date={date}", 1L, early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }
}
