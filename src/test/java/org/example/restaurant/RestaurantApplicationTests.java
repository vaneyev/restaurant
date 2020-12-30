package org.example.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurant.model.Menu;
import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.MenuRepository;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.DateTimeService;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
class RestaurantApplicationTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final LocalDateTime early = LocalDateTime.of(2020, 12, 25, 10, 0);
    private final LocalDateTime late = LocalDateTime.of(2020, 12, 25, 12, 0);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private DateTimeService dateTimeService;

    @Test
    void addVote() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(
                put("/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(2L))
                        .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, 2L, early.toLocalDate());
        Optional<Vote> addedVote = voteRepository.findOne(Example.of(vote));
        assertTrue(addedVote.isPresent());
    }

    @Test
    void removeVote() throws Exception {
        dateTimeService.setCustom(early);
        mockMvc.perform(put("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1L))
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isNoContent());
        dateTimeService.setSystem();
        Vote vote = new Vote(2L, 1L, early.toLocalDate());
        Optional<Vote> removedVote = voteRepository.findOne(Example.of(vote));
        assertFalse(removedVote.isPresent());
    }

    @Test
    void lateVote() throws Exception {
        dateTimeService.setCustom(late);
        mockMvc.perform(put("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1L))
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isBadRequest());
        dateTimeService.setSystem();
    }

    @Test
    void getMenu() throws Exception {
        Menu expectedMenu = menuRepository.findById(1L).orElse(null);
        assertNotNull(expectedMenu);
        MvcResult result = mockMvc.perform(get("/menus/1")
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Menu actualMenu = mapper.readValue(content, Menu.class);
        assertTrue(new ReflectionEquals(expectedMenu, "dishes").matches(actualMenu));
        assertEquals(expectedMenu.getDishes().size(), actualMenu.getDishes().size());
        for (int i = 0; i < expectedMenu.getDishes().size(); i++) {
            assertTrue(new ReflectionEquals(expectedMenu.getDishes().get(i)).matches(actualMenu.getDishes().get(i)));
        }
    }
}
