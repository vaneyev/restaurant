package org.example.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.restaurant.model.Menu;
import org.example.restaurant.model.MenuItem;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.MenuRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MenuControllerTests extends AbstractControllerTests {
    private final Menu menu1 = new Menu(1L, restaurant1, LocalDate.of(2020, 12, 25));
    private final Menu menu3 = new Menu(3L, restaurant2, LocalDate.of(2020, 12, 25));
    private final Menu newMenu = new Menu(null, restaurant1, LocalDate.of(2020, 12, 27));
    private final Menu createdMenu = new Menu(4L, restaurant1, LocalDate.of(2020, 12, 27));
    private final Menu notValidMenu = new Menu(4L, null, null);
    private final Menu menuWithNullRestaurantId = new Menu(4L, new Restaurant(null, "Third"), LocalDate.of(2020, 12, 27));
    private final Menu menuWithNotValidDish = new Menu(4L, restaurant1, LocalDate.of(2020, 12, 27));
    private final Menu updatedMenu = new Menu(1L, restaurant1, LocalDate.of(2020, 12, 25));
    private final MenuItem menuItem1 = new MenuItem(1L, menu1, dish1, 10);
    private final MenuItem menuItem2 = new MenuItem(2L, menu1, dish2, 2);
    private final MenuItem menuItem4 = new MenuItem(4L, menu3, dish4, 15);
    private final MenuItem createdMenuItem = new MenuItem(5L, createdMenu, dish3, 7);
    private final MenuItem notValidMenuItem = new MenuItem(5L, createdMenu, null, null);
    private final MenuItem updatedMenuItem = new MenuItem(1L, updatedMenu, dish1, 9);
    private final MenuItem newMenuItem = new MenuItem(null, updatedMenu, dish3, 7);

    {
        menu1.setMenuItems(List.of(menuItem1, menuItem2));
        menu3.setMenuItems(List.of(menuItem4));
        newMenu.setMenuItems(List.of(newMenuItem));
        createdMenu.setMenuItems(List.of(createdMenuItem));
        notValidMenu.setMenuItems(List.of(createdMenuItem));
        menuWithNotValidDish.setMenuItems(List.of(notValidMenuItem));
        updatedMenu.setMenuItems(List.of(updatedMenuItem, menuItem2, createdMenuItem));
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
        assertThat(actual).usingRecursiveComparison().isEqualTo(menu1);
    }

    @Test
    void getNotFound() throws Exception {
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
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void create() throws Exception {
        String result = mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newMenu))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Menu actual = mapper.readValue(result, Menu.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(createdMenu);
    }

    @Test
    void createWithId() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdMenu))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
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
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithNullRestaurantId() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menuWithNullRestaurantId))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithUnique() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menu1))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createWithNotValidDish() throws Exception {
        mockMvc.perform(post("/menus")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menuWithNotValidDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/menus/{menu}", updatedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedMenu))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Menu> actual = menuRepository.getById(updatedMenu.getId());
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().ignoringFields("menuItems.menu", "restaurant").isEqualTo(updatedMenu);
        assertThat(actual.get().getRestaurant()).isEqualTo(updatedMenu.getRestaurant());
    }

    @Test
    void updateUnauthorized() throws Exception {
        mockMvc.perform(put("/menus/{menu}", updatedMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedMenu))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateNotFound() throws Exception {
        mockMvc.perform(put("/menus/{menu}", createdMenu.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdMenu))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Menu> actual = menuRepository.getById(createdMenu.getId());
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().ignoringFields("menuItems.menu", "restaurant").isEqualTo(createdMenu);
        assertThat(actual.get().getRestaurant()).isEqualTo(createdMenu.getRestaurant());
    }

    @Test
    void updateNotValidDish() throws Exception {
        mockMvc.perform(put("/menus/{menu}", menu1.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(menuWithNotValidDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
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
    void deleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/menus/{menu}", menu1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete("/menus/{menu}", createdMenu.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
