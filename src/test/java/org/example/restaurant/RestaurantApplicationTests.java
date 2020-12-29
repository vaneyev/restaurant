package org.example.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurant.model.Menu;
import org.example.restaurant.model.Vote;
import org.example.restaurant.repository.MenuRepository;
import org.example.restaurant.repository.VoteRepository;
import org.example.restaurant.service.ClockService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Example;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
class RestaurantApplicationTests {
    private final ObjectMapper mapper = new ObjectMapper();
    private final Clock earlyClock = Clock.fixed(LocalDateTime.of(2020, 12, 25, 10, 0).toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));
    private final Clock lateClock = Clock.fixed(LocalDateTime.of(2020, 12, 25, 12, 0).toInstant(ZoneOffset.UTC), ZoneId.of("UTC"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private VoteRepository voteRepository;

    @MockBean
    private ClockService clockService;

    @Test
    void addVote() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(clockService.getClock()).thenReturn(earlyClock);
        mockMvc.perform(
                put("/votes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(2L))
                        .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isNoContent());
        Vote vote = new Vote(2L, 2L, LocalDate.now(earlyClock));
        Optional<Vote> addedVote = voteRepository.findOne(Example.of(vote));
        assertTrue(addedVote.isPresent());
    }

    @Test
    void removeVote() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(clockService.getClock()).thenReturn(earlyClock);
        mockMvc.perform(put("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1L))
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isNoContent());
        Vote vote = new Vote(2L, 1L, LocalDate.now(earlyClock));
        Optional<Vote> removedVote = voteRepository.findOne(Example.of(vote));
        assertFalse(removedVote.isPresent());
    }

    @Test
    void lateVote() throws Exception {
        MockitoAnnotations.openMocks(this);
        when(clockService.getClock()).thenReturn(lateClock);
        mockMvc.perform(put("/votes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(1L))
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
