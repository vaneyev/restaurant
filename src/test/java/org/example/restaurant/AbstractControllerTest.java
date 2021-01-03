package org.example.restaurant;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public abstract class AbstractControllerTest {
    protected final LocalDateTime early = LocalDateTime.of(2020, 12, 25, 10, 0);
    protected final LocalDateTime late = LocalDateTime.of(2020, 12, 25, 12, 0);


    protected final ObjectMapper mapper = new ObjectMapper();

    @Autowired
    protected MockMvc mockMvc;
}
