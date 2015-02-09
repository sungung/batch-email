package com.sungung.web;

import com.sungung.data.service.BrewerService;
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
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.jasperreports.AbstractJasperReportsSingleFormatView;
import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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

    @Autowired
    BrewerService brewerService;

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

    @RequestMapping("/jasper")
    public ModelAndView report(HttpServletRequest request) {

        Map<String, Object> model = new HashMap<String, Object>();
        model.put("REPORT.SOURCE", brewerService.findAll(null));

        AbstractJasperReportsSingleFormatView jasperReportsSingleFormatView = new JasperReportsPdfView();
        Properties header = new Properties();
        header.put("Content-Disposition", "inline; filename=report.pdf");
        jasperReportsSingleFormatView.setHeaders(header);
        jasperReportsSingleFormatView.setUrl("classpath:jrxml/list.jrxml");
        jasperReportsSingleFormatView.setApplicationContext(
                WebApplicationContextUtils.getRequiredWebApplicationContext(request.getSession().getServletContext())
        );
        return new ModelAndView(jasperReportsSingleFormatView, model);


    }

}
