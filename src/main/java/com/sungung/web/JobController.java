package com.sungung.web;

import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RestController
public class JobController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    @Qualifier("sendEmailJob")
    Job sendEmailJob;

    @Autowired
    JobExplorer jobExplorer;

    @RequestMapping("/sendEmailJob")
    public void runningJob() throws JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException, JobExecutionAlreadyRunningException {

        // Expecting incrementer in job configuration manage uniqueness of job instance
        // but it appears to be not working so passing timestamp whenever job is launched
        Map<String, JobParameter> parameters = new HashMap<String, JobParameter>();
        parameters.put("timestamp", new JobParameter(new Date().getTime()));
        jobLauncher.run(sendEmailJob, new JobParameters(parameters));
    }

    @RequestMapping("/jobInstances/{jobName}")
    public List<JobInstance> getJobInstances(@PathVariable String jobName) {
        return jobExplorer.getJobInstances(jobName, 0, 99);
    }
}
