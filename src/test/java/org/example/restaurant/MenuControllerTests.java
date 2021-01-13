package org.example.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.restaurant.model.Dish;
import org.example.restaurant.model.Menu;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTests extends AbstractControllerTests {

    private final Menu menu1 = new Menu(1L, restaurant1, LocalDate.of(2020, 12, 25));
    private final Menu menu3 = new Menu(3L, restaurant2, LocalDate.of(2020, 12, 25));
    private final Menu createdMenu = new Menu(4L, restaurant1, LocalDate.of(2020, 12, 27));
    private final Menu notValidMenu = new Menu(4L, null, null);
    private final Menu menuWithNullRestaurantId = new Menu(4L, new Restaurant(null, "Third"), LocalDate.of(2020, 12, 27));
    private final Menu menuWithNotValidDish = new Menu(4L, restaurant1, LocalDate.of(2020, 12, 27));
    private final Menu updatedMenu = new Menu(1L, restaurant1, LocalDate.of(2020, 12, 25));
    private final Dish dish1 = new Dish(1L, menu1, "Fish", 10);
    private final Dish dish2 = new Dish(2L, menu1, "Potato", 2);
    private final Dish dish4 = new Dish(4L, menu3, "Beacon", 15);
    private final Dish createdDish = new Dish(5L, createdMenu, "Oranges", 7);
    private final Dish notValidDish = new Dish(5L, createdMenu, "N", null);
    private final Dish updatedDish = new Dish(1L, updatedMenu, "Fish", 9);
    private final Dish newDish = new Dish(5L, updatedMenu, "Oranges", 7);

    {
        menu1.setDishes(List.of(dish1, dish2));
        menu3.setDishes(List.of(dish4));
        createdMenu.setDishes(List.of(createdDish));
        notValidMenu.setDishes(List.of(createdDish));
        menuWithNotValidDish.setDishes(List.of(notValidDish));
        updatedMenu.setDishes(List.of(updatedDish, dish2, newDish));
    }

    @Autowired
    private MenuRepository menuRepository;

    @Test
    void getOne() throws Exception {
        String result = mockMvc.perform(get("/menus/{menu}", menu1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Menu actual = mapper.readValue(result, Menu.class);
        assertMenuEquals(menu1, actual);
    }

    @Test
    void getOneNotFound() throws Exception {
        mockMvc.perform(get("/menus/{menu}", 4L)
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllByDate() throws Exception {
        List<Menu> expected = List.of(menu1, menu3);
        String result = mockMvc.perform(get("/menus?date={date}", early.toLocalDate())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Menu> actual = mapper.readValue(result, new TypeReference<>() {
        });
        assertListsOfMenuEquals(expected, actual);
    }

    @Test
    void create() throws Exception {
        String result = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdMenu))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Menu actual = mapper.readValue(result, Menu.class);
        assertMenuEquals(createdMenu, actual);
    }

    @Test
    void createUnauthorized() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdMenu))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createNotValid() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(notValidMenu))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithNullRestaurantId() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menuWithNullRestaurantId))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithUnique() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menu1))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void createWithNotValidDish() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menuWithNotValidDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/menus/{menu}", updatedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedMenu.getDishes()))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Menu> actual = menuRepository.getById(updatedMenu.getId());
        assertTrue(actual.isPresent());
        assertMenuEquals(updatedMenu, actual.get());
    }

    @Test
    void updateUnauthorized() throws Exception {
        mockMvc.perform(put("/menus/{menu}", updatedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedMenu.getDishes()))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateNotFound() throws Exception {
        mockMvc.perform(put("/menus/{menu}", createdMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdMenu.getDishes()))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateNotValidDish() throws Exception {
        mockMvc.perform(put("/menus/{menu}", menu1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menuWithNotValidDish.getDishes()))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteOne() throws Exception {
        mockMvc.perform(delete("/menus/{menu}", menu1.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(menuRepository.findById(menu1.getId()).isEmpty());
    }

    @Test
    void deleteOneUnauthorized() throws Exception {
        mockMvc.perform(delete("/menus/{menu}", menu1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOneNotFound() throws Exception {
        mockMvc.perform(delete("/menus/{menu}", createdMenu.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void assertMenuEquals(Menu expected, Menu actual) {
        assertTrue(new ReflectionEquals(expected, "dishes").matches(actual));
        assertEquals(expected.getDishes().size(), actual.getDishes().size());
        for (int i = 0; i < expected.getDishes().size(); i++) {
            assertTrue(new ReflectionEquals(expected.getDishes().get(i)).matches(actual.getDishes().get(i)));
        }
    }

    private void assertListsOfMenuEquals(List<Menu> expected, List<Menu> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertMenuEquals(expected.get(i), actual.get(i));
        }
    }
}
