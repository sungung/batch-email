package com.sungung.batch;

import com.sungung.data.model.Brewer;
import com.sungung.data.service.BrewerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.mail.javamail.MimeMessageItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Configuration
@EnableBatchProcessing
public class EmailJobConfig {

    private static final Log LOG = LogFactory.getLog(EmailJobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BrewerService brewerService;

    @Autowired
    private JobRepository jobRepository;

    /*
    @Bean
    public SimpleAsyncTaskExecutor taskExecutor() {
        return new SimpleAsyncTaskExecutor();
    }

    @Bean
    public JobDetailBean jobDetailBean() {
        return new JobDetailBean();
    }

    @Bean
    public SimpleJobLauncher jobLauncher() {
        SimpleJobLauncher jobLauncher = new SimpleJobLauncher();
        jobLauncher.setJobRepository(jobRepository);
        jobLauncher.setTaskExecutor(taskExecutor());
        return jobLauncher;
    }*/

    @Bean
    public Job sendEmailJob() {
        return jobBuilderFactory
                .get("sendEmailJob")
                .incrementer(new RunIdIncrementer())
                .listener(sendEmailListener())
                .start(sendEmailStep())
                .build();
    }

    @Bean
    public Step sendEmailStep() {
        return stepBuilderFactory
                .get("sendEmailStep")
                .<Brewer, MimeMessage>chunk(10)
                .reader(brewerReader())
                .processor(brewerToMimeMessageProcessor())
                .writer(brewerWriter())
                .listener(logSendEmailListener())
                .build();
    }

    @Bean
    public StepExecutionListener logSendEmailListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                LOG.info("   %% Before step %%");
                LOG.info(stepExecution.toString());
                LOG.info("   %%%%%%%%%%%%%%%%%");
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                LOG.info("   %% After step %%");
                LOG.info(stepExecution.toString());
                LOG.info("   %%%%%%%%%%%%%%%%");
                return ExitStatus.COMPLETED;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Bean
    @StepScope
    public ItemWriter<MimeMessage> brewerWriter() {
        MimeMessageItemWriter writer = new MimeMessageItemWriter();
        writer.setJavaMailSender(javaMailSender);
        return writer;
    }

    @Bean
    @StepScope
    public ItemProcessor<Brewer, MimeMessage> brewerToMimeMessageProcessor() {
        return new ItemProcessor<Brewer, MimeMessage>() {
            @Override
            public MimeMessage process(Brewer brewer) throws Exception {
                MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()));
                message.setFrom(new InternetAddress("admin@sungung.com"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(brewer.getEmail()));
                message.setSubject("To Victoria Brewer");
                message.setText("Thank you for your wonderful bear");
                return message;
            }
        };
    }

    /**
     * Reader scope should be in step scope, otherwise your bean will not run the code inside from controller.
     * Because bean is created when app is started.
     * @return
     */
    @Bean
    @StepScope
    public ItemReader<Brewer> brewerReader() {
        List<Brewer> brewers = brewerService.findAll(null);
        return new IteratorItemReader<Brewer>(brewers);
    }

    @Bean
    public JobExecutionListener sendEmailListener() {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(JobExecution jobExecution) {

                LOG.info("******** Before Job ********");
                LOG.info(jobExecution.toString());
                LOG.info("****************************");
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                LOG.info("******** After Job ********");
                LOG.info(jobExecution.toString());
                LOG.info("***************************");
            }
        };
    }

}
