package com.sungung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
@SpringApplicationConfiguration(classes = Application.class)
public class WebControllerTests {

    @Autowired
    private WebApplicationContext wax;
    private MockMvc mock;

    @Before
    public void setup() {
        this.mock = MockMvcBuilders.webAppContextSetup(this.wax).build();
    }

    @Test
    public void testDefault() throws Exception {
        //there is no controller for home path
        this.mock.perform(get("/")).andExpect(status().isNotFound());
        //verify content is pdf
        this.mock.perform(get("/jasper")).andExpect(status().isOk())
                .andExpect(header().string("Content-Type","application/pdf"));
        this.mock.perform(get("/sendEmailJob")).andExpect(status().isOk());
        //verify json content contains the payload
        this.mock.perform(get("/jobInstances/sendEmailJob")).andExpect(status().isOk())
                .andExpect(content().string(containsString("\"jobName\":\"sendEmailJob\"")));
    }



}
