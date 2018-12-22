package org.rest.server;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.Charset;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK, classes = RestServerApplication.class)
@WebAppConfiguration
@AutoConfigureMockMvc
public class ExecutionControllerTests {

    private MediaType responseContentType = new MediaType(
            MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Before
    public void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testGetExecute() throws Exception {
        mockMvc.perform(post("/execute").contentType(MediaType.APPLICATION_JSON).content("[1,2,13,4]"));

        mockMvc.perform(get("/execute"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(responseContentType))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$.[3].id").value(13))
                .andExpect(jsonPath("$.[3].execution").value("Hello, 13! 13*2 = 26."))
                .andExpect(jsonPath("$.[3].ts").isNumber());

        mockMvc.perform(get("/execute").contentType(MediaType.APPLICATION_JSON).content("[2,4]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(responseContentType))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id").value(2))
                .andExpect(jsonPath("$.[0].execution").value("Hello, 2! 2*2 = 4."))
                .andExpect(jsonPath("$.[0].ts").isNumber())
                .andExpect(jsonPath("$.[1].id").value(4))
                .andExpect(jsonPath("$.[1].execution").value("Hello, 4! 4*2 = 8."))
                .andExpect(jsonPath("$.[1].ts").isNumber());
    }

    @Test
    public void testPostExecute() throws Exception {

        mockMvc.perform(post("/execute").contentType(MediaType.APPLICATION_JSON).content("[1,2]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(responseContentType))
                .andExpect(content().string("{\"1\":\"Hello, 1! 1*2 = 2.\",\"2\":\"Hello, 2! 2*2 = 4.\"}"));
    }

    @Test
    public void testPostExecuteSimilar() throws Exception {

        mockMvc.perform(post("/execute").contentType(MediaType.APPLICATION_JSON).content("[1,1]"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(responseContentType))
                .andExpect(content().string("{\"1\":\"Hello, 1! 1*2 = 2.Hello, 1! 1*2 = 2.\"}"));
    }
}
