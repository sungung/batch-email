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

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Configuration
@EnableBatchProcessing
public class JobConfig {

    private static final Log LOG = LogFactory.getLog(JobConfig.class);

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private BrewerService brewerService;

    @Bean
    public Job sendEmailJob() {
        return jobBuilderFactory
                .get("sendEmailJob")
                .listener(sendEmailListener())
                .start(sendEmailStep())
                .build();
    }

    @Bean
    public Step sendEmailStep() {
        return stepBuilderFactory
                .get("sendEmailStep")
                .<Brewer, MimeMessage>chunk(1)
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
                return ExitStatus.UNKNOWN;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Bean
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

    @Bean
    public ItemReader<Brewer> brewerReader() {
        return new IteratorItemReader<Brewer>(
            brewerService.findAll(null)
        );
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