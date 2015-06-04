package com.sungung;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.JobOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class XmlFileTests {

    /*
    Perform common monitoring tasks such as stopping, restarting, or summraizing a Job
     */
    @Autowired
    JobOperator operator;

    /*
    A simple interface for launching a Job with a given set of JobParameters
     */
    @Autowired
    JobLauncher launcher;

    /*
    Keeping track of which jobs are available in the context
     */
    @Autowired
    JobRegistry registry;

    @Autowired
    @Qualifier("xmlJob")
    Job job;

    @Before
    public void setup() throws Exception {
        if (!registry.getJobNames().contains(job.getName())) {
            registry.register(new ReferenceJobFactory(job));
        }
    }

    @Test
    public void testRun() throws Exception {
        JobExecution execution = launcher.run(
                job,
                (new JobParametersBuilder().addLong("timestamp", new Date().getTime()).toJobParameters())
        );
    }

    @Test
    public void testStartsStopResumeJob() throws Exception {
        String params = new JobParametersBuilder().addLong("timestamp", new Date().getTime()).toJobParameters().toString();

        long executionId = operator.start(job.getName(), params);
        assertEquals(params, operator.getParameters(executionId));
        //stopAndCheckStatus(executionId);

        //long resumedExecutionId = operator.restart(executionId);
        //assertEquals(params, operator.getParameters(resumedExecutionId));

    }

    private void stopAndCheckStatus(long executionId) throws Exception {
        Thread.sleep(1000);
        Set<Long> runningExecutions = operator.getRunningExecutions(job.getName());
        assertTrue("Wrong executions: " + runningExecutions + "expected", runningExecutions.contains(executionId));
        assertTrue("Wrong summary: " + operator.getSummary(executionId), operator.getSummary(executionId).contains(BatchStatus.STARTED.toString()));
    }

}
