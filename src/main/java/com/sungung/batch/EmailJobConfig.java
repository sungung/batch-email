package com.sungung.batch;

import com.sungung.data.model.Brewer;
import com.sungung.data.service.BrewerService;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.mail.javamail.MimeMessageItemWriter;
import org.springframework.batch.item.support.IteratorItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Bean
    public Job sendEmailJob() {
        return jobBuilderFactory
                .get("sendEmailJob")
                .incrementer(new RunIdIncrementer())
                //.listener(sendEmailListener())
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
                //.taskExecutor(new SimpleAsyncTaskExecutor())
                //.throttleLimit(10)
                .build();
    }

    @Bean
    public StepExecutionListener logSendEmailListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
            }
            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                LOG.info("******** Step >>>> ********");
                LOG.info(stepExecution.toString());
                LOG.info(Thread.currentThread().getId());
                LOG.info("******** <<<< Step ********");
                return ExitStatus.COMPLETED;
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

                Map<String, Object> model = new HashMap<String, Object>();
                model.put("REPORT.SOURCE", brewerService.findAll(null));

                ClassPathResource jasperPath = new ClassPathResource("jrxml/list.jasper");

                JasperPrint jasperPrint = JasperFillManager.fillReport((new ClassPathResource("jrxml/list.jasper")).getInputStream(),
                        new HashMap<String, Object>(),
                        new JRBeanCollectionDataSource(brewerService.findAll(null)));

                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, byteArrayOutputStream);
                DataSource attchement =  new ByteArrayDataSource(byteArrayOutputStream.toByteArray(), "application/pdf");

                MimeMessage message = new MimeMessage(Session.getDefaultInstance(System.getProperties()));

                message.setFrom(new InternetAddress("admin@sungung.com"));
                message.addRecipient(Message.RecipientType.TO, new InternetAddress(brewer.getEmail()));
                message.setSubject("To Victoria Brewer");

                MimeBodyPart messagePart = new MimeBodyPart();
                messagePart.setText("Please find Victoria brewer friends");

                MimeBodyPart attachementPart = new MimeBodyPart();
                attachementPart.setDataHandler(new DataHandler(attchement));
                attachementPart.setFileName("brewer list.pdf");

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messagePart);
                multipart.addBodyPart(attachementPart);
                message.setContent(multipart);

                /*
                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true);
                mimeMessageHelper.setFrom("admin@sungung.com");
                mimeMessageHelper.setTo(brewer.getEmail());
                mimeMessageHelper.setSubject("To Victoria Brewer");
                mimeMessageHelper.addAttachment("Victoria brewer list.pdf", attchement);
                */
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
            }

            @Override
            public void afterJob(JobExecution jobExecution) {
                LOG.info("******** After Job ********");
                LOG.info(jobExecution.toString());
            }
        };
    }

}
