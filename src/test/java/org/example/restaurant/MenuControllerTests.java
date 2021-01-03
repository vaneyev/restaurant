package org.example.restaurant;

import org.example.restaurant.model.Menu;
import org.example.restaurant.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTests extends AbstractControllerTest {

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void getOne() throws Exception {
        Menu expectedMenu = menuRepository.findById(1L).orElse(null);
        assertNotNull(expectedMenu);
        MvcResult result = mockMvc.perform(get("/menus/1")
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        String content = result.getResponse().getContentAsString();
        Menu actualMenu = mapper.readValue(content, Menu.class);
        assertMenuEquals(expectedMenu, actualMenu);
    }

    @Test
    void getAllByDate() throws Exception {
        List<Menu> expectedMenus = menuRepository.findByDate(early.toLocalDate());
        MvcResult result = mockMvc.perform(get("/menus/dates/{date}", early.toLocalDate().toString())
                .with(httpBasic("User", "pass")))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
    }

    private void assertMenuEquals(Menu expected, Menu actual) {
        assertTrue(new ReflectionEquals(expected, "dishes").matches(actual));
        assertEquals(expected.getDishes().size(), actual.getDishes().size());
        for (int i = 0; i < expected.getDishes().size(); i++) {
            assertTrue(new ReflectionEquals(expected.getDishes().get(i)).matches(actual.getDishes().get(i)));
        }
    }
}
