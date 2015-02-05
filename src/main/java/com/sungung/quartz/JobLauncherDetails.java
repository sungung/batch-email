package com.sungung.quartz;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.JobLocator;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Date;
import java.util.Map;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
public class JobLauncherDetails extends QuartzJobBean {

    static final String JOB_NAME = "jobName";

    private static Log logger = LogFactory.getLog(JobLauncherDetails.class);

    private JobLocator jobLocator;

    private JobLauncher jobLauncher;

    public void setJobLocator(JobLocator jobLocator) {
        this.jobLocator = jobLocator;
    }

    public void setJobLauncher(JobLauncher jobLauncher) {
        this.jobLauncher = jobLauncher;
    }

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)  {
        Map<String, Object> jobDataMap = jobExecutionContext.getMergedJobDataMap();
        String jobName = (String)jobDataMap.get(JOB_NAME);
        logger.info("Quartz trigger firing with Spring Batch jobName="+jobName);
        JobParameters jobParameters = getJobParametersFromJobMap(jobDataMap);
        try {
            jobLauncher.run(jobLocator.getJob(jobName), jobParameters);
        } catch (JobExecutionException e) {
            logger.error("Could not execute job.",e);
        }
    }

    private JobParameters getJobParametersFromJobMap(Map<String, Object> jobDataMap) {
        JobParametersBuilder builder = new JobParametersBuilder();

        for (Map.Entry<String, Object> entry : jobDataMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String && !key.equals(JOB_NAME)) {
                builder.addString(key, (String) value);
            }
            else if (value instanceof Float || value instanceof Double) {
                builder.addDouble(key, ((Number) value).doubleValue());
            }
            else if (value instanceof Integer || value instanceof Long) {
                builder.addLong(key, ((Number)value).longValue());
            }
            else if (value instanceof Date) {
                builder.addDate(key, (Date) value);
            }
            else {
                logger.debug("JobDataMap contains values which are not job parameters (ignoring).");
            }
        }

        return builder.toJobParameters();
    }
}
