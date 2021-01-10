package org.example.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.restaurant.model.Restaurant;
import org.example.restaurant.repository.RestaurantRepository;
import org.junit.jupiter.api.Test;
import org.mockito.internal.matchers.apachecommons.ReflectionEquals;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestaurantControllerTests extends AbstractControllerTests {
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
        assertTrue(new ReflectionEquals(restaurant1).matches(actual));
    }

    @Test
    public void getOneNotFound() throws Exception {
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
        assertListsOfRestaurantEquals(expected, actual);
    }

    @Test
    void create() throws Exception {
        String result = mockMvc.perform(post("/restaurants")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Restaurant actual = mapper.readValue(result, Restaurant.class);
        assertTrue(new ReflectionEquals(createdRestaurant).matches(actual));
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
                .andExpect(status().isBadRequest());
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
        assertTrue(new ReflectionEquals(updatedRestaurant).matches(actual.get()));
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
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateNotFound() throws Exception {
        mockMvc.perform(put("/restaurants/{restaurant}", absentRestaurant.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(absentRestaurant))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
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
    void deleteOneUnauthorized() throws Exception {
        mockMvc.perform(delete("/restaurants/{restaurant}", restaurant1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteOneNotFound() throws Exception {
        mockMvc.perform(delete("/restaurants/{restaurant}", absentRestaurant.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    private void assertListsOfRestaurantEquals(List<Restaurant> expected, List<Restaurant> actual) {
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertTrue(new ReflectionEquals(expected.get(i)).matches(actual.get(i)));
        }
    }
}
