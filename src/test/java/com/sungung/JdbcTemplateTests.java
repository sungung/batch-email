package com.sungung;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertTrue;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class JdbcTemplateTests {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void connectionTest() {
        long count = jdbcTemplate.queryForObject("select count(*) from brewer", Long.class);
        assertTrue(count > 0);
    }


}
