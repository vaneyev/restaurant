package org.example.restaurant;

import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.DateTimeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VoteControllerTests extends AbstractControllerTest {
    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DateTimeService dateTimeService;

    @Test
    void add() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(
                put("/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(2L))
                        .with(userAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, 2L, early.toLocalDate());
        Optional<Vote> addedVote = voteRepository.findOne(Example.of(vote));
        assertTrue(addedVote.isPresent());
    }

    @Test
    void remove() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(put("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1L))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, 1L, early.toLocalDate());
        Optional<Vote> removedVote = voteRepository.findOne(Example.of(vote));
        assertFalse(removedVote.isPresent());
    }

    @Test
    void late() throws Exception {
        dateTimeService.setCustom(late);
        mockMvc.perform(put("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1L))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dateTimeService.setSystem();
    }

    @Test
    void getPresent() throws Exception {
        mockMvc.perform(get("/votes/restaurants/{restaurant}/dates/{date}", 1L, early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void getAbsent() throws Exception {
        mockMvc.perform(get("/votes/restaurants/{restaurant}/dates/{date}", 1L, early.plusDays(1).toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void getCount() throws Exception {
        mockMvc.perform(get("/votes/count/restaurants/{restaurant}/dates/{date}", 1L, early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("2"));
    }
}
