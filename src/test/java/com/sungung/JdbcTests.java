package com.sungung;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

import static junit.framework.Assert.assertEquals;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class JdbcTests {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("jdbcJob")
    private Job job;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    public void test() throws Exception {

        int first = jdbcTemplate.queryForObject("select count(*) from brewer where credit>200", Integer.class);
        JobExecution jobExecution = jobLauncher.run(job, getJobParameters(200.0));

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(first, jobExecution.getStepExecutions().iterator().next().getWriteCount());

    }

    private JobParameters getJobParameters(double amount) {
        return new JobParametersBuilder()
                .addLong("timestamp", new Date().getTime())
                .addDouble("credit", amount)
                .toJobParameters();
    }

}
