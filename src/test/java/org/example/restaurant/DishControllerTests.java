package org.example.restaurant;

import com.fasterxml.jackson.core.type.TypeReference;
import org.example.restaurant.model.Dish;
import org.example.restaurant.repository.DishRepository;
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

public class DishControllerTests extends AbstractControllerTests {
    private final Dish newDish = new Dish(null, "Oranges");
    private final Dish createdDish = new Dish(5L, "Oranges");
    private final Dish updatedDish = new Dish(1L, "Herring");
    private final Dish notValidDish = new Dish(1L, "N");
    private final Dish absentDish = new Dish(5L, "Absent dish");

    @Autowired
    DishRepository dishRepository;

    @Test
    public void getOne() throws Exception {
        String result = mockMvc.perform(get("/dishes/{dish}", dish1.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Dish actual = mapper.readValue(result, Dish.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(dish1);
    }

    @Test
    public void getNotFound() throws Exception {
        mockMvc.perform(get("/dishes/{dish}", absentDish.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void getAll() throws Exception {
        List<Dish> expected = List.of(dish1, dish2, dish3, dish4);
        String result = mockMvc.perform(get("/dishes")
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        List<Dish> actual = mapper.readValue(result, new TypeReference<>() {
        });
        assertThat(actual).usingRecursiveComparison().isEqualTo(expected);
    }

    @Test
    void create() throws Exception {
        String result = mockMvc.perform(post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(newDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        Dish actual = mapper.readValue(result, Dish.class);
        assertThat(actual).usingRecursiveComparison().isEqualTo(createdDish);
    }

    @Test
    void createWithId() throws Exception {
        mockMvc.perform(post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void createUnauthorized() throws Exception {
        mockMvc.perform(post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(createdDish))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void createNotValid() throws Exception {
        mockMvc.perform(post("/dishes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(notValidDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void update() throws Exception {
        mockMvc.perform(put("/dishes/{dish}", updatedDish.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Dish> actual = dishRepository.findById(updatedDish.getId());
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(updatedDish);
    }

    @Test
    void updateUnauthorized() throws Exception {
        mockMvc.perform(put("/dishes/{dish}", updatedDish.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedDish))
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void updateNotValid() throws Exception {
        mockMvc.perform(put("/dishes/{dish}", notValidDish.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(notValidDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateNotFound() throws Exception {
        mockMvc.perform(put("/dishes/{dish}", absentDish.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(absentDish))
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        Optional<Dish> actual = dishRepository.findById(absentDish.getId());
        assertTrue(actual.isPresent());
        assertThat(actual.get()).usingRecursiveComparison().isEqualTo(absentDish);
    }

    @Test
    void deleteOne() throws Exception {
        mockMvc.perform(delete("/dishes/{dish}", dish1.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNoContent());
        assertTrue(dishRepository.findById(dish1.getId()).isEmpty());
    }

    @Test
    void deleteUnauthorized() throws Exception {
        mockMvc.perform(delete("/dishes/{dish}", restaurant1.getId())
                .with(userAuth))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    void deleteNotFound() throws Exception {
        mockMvc.perform(delete("/dishes/{dish}", absentDish.getId())
                .with(adminAuth))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}
