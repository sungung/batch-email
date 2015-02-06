package com.sungung;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class XmlFileTests {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("xmlJob")
    Job xmlJob;

    @Test
    public void testRun() throws Exception {
        JobExecution execution = jobLauncher.run(xmlJob, (new JobParametersBuilder().addLong("timestamp", new Date().getTime()).toJobParameters()));
    }

}
