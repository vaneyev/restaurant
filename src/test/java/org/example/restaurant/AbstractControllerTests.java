package org.example.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.restaurant.model.Dish;
import org.example.restaurant.model.Restaurant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql({"/schema.sql", "/data.sql"})
public abstract class AbstractControllerTests {
    protected final RequestPostProcessor adminAuth = httpBasic("Admin", "pass");
    protected final RequestPostProcessor userAuth = httpBasic("User", "pass");

    protected final Restaurant restaurant1 = new Restaurant(1L, "First");
    protected final Restaurant restaurant2 = new Restaurant(2L, "Second");

    protected final Dish dish1 = new Dish(1L, "Fish");
    protected final Dish dish2 = new Dish(2L, "Potato");
    protected final Dish dish3 = new Dish(3L, "Tomatoes");
    protected final Dish dish4 = new Dish(4L, "Beacon");

    protected final LocalDateTime early = LocalDateTime.of(2020, 12, 25, 10, 0);
    protected final LocalDateTime late = LocalDateTime.of(2020, 12, 25, 12, 0);

    protected final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
}
