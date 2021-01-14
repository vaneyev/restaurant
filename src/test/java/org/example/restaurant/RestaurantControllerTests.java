package org.example.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantControllerTests extends AbstractControllerTests {
    private final Restaurant newRestaurant = new Restaurant(null, "New restaurant");
    private final Restaurant createdRestaurant = new Restaurant(3L, "New restaurant");
    private final Restaurant updatedRestaurant = new Restaurant(1L, "First and biggest");
    private final Restaurant notValidRestaurant = new Restaurant(1L, "N");
    private final Restaurant absentRestaurant = new Restaurant(3L, "Absent restaurant");

    @Autowired
    RestaurantRepository restaurantRepository;

    @Test
    public void getOne() throws Exception {
        String result = mockMvc.perform(get("/restaurants/{restaurant}", restaurant1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Restaurant actual = mapper.readValue(result, Restaurant.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(restaurant1);
    }

    @Test
    public void getNotFound() throws Exception {
        mockMvc.perform(get("/restaurants/{restaurant}", absentRestaurant.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAll() throws Exception {
        List<Restaurant> expected = List.of(restaurant1, restaurant2);
        String result = mockMvc.perform(get("/restaurants")
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Restaurant> actual = mapper.readValue(result, new TypeReference<>() {
        });
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void create() throws Exception {
        String result = mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Restaurant actual = mapper.readValue(result, Restaurant.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(createdRestaurant);
    }

    @Test
    void createWithId() throws Exception {
        mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createUnauthorized() throws Exception {
        mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdRestaurant))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createNotValid() throws Exception {
        mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(notValidRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/restaurants/{restaurant}", updatedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Restaurant> actual = restaurantRepository.findById(updatedRestaurant.getId());
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(updatedRestaurant);
    }

    @Test
    void updateUnauthorized() throws Exception {
        mockMvc.perform(put("/restaurants/{restaurant}", updatedRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedRestaurant))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateNotValid() throws Exception {
        mockMvc.perform(put("/restaurants/{restaurant}", notValidRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(notValidRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateNotFound() throws Exception {
        mockMvc.perform(put("/restaurants/{restaurant}", absentRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(absentRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Restaurant> actual = restaurantRepository.findById(absentRestaurant.getId());
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(absentRestaurant);
    }

    @Test
    void deleteOne() throws Exception {
        mockMvc.perform(delete("/restaurants/{restaurant}", restaurant1.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(restaurantRepository.findById(restaurant1.getId()).isEmpty());
    }

    @Test
    void deleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/restaurants/{restaurant}", restaurant1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete("/restaurants/{restaurant}", absentRestaurant.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
