package com.sungung.batch;

import com.sungung.data.model.StationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.FieldExtractor;
import org.springframework.batch.item.file.transform.LineAggregator;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import java.net.MalformedURLException;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Configuration
public class XmlToFlatFileJobConfig {

    private final static Logger logger = LoggerFactory.getLogger(XmlToFlatFileJobConfig.class);

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job xmlJob(Step xmlJobStep) {
        return jobBuilderFactory
                .get("xmlJob")
                .start(xmlJobStep)
                .build();
    }

    @Bean
    public Step xmlJobStep(ItemReader xmlItemReader, ItemWriter xmlItemWriter) {
        return stepBuilderFactory
                .get("fileJobStep")
                .chunk(1)
                .reader(xmlItemReader)
                .writer(xmlItemWriter)
                .listener(snailListener())
                .build();
    }

    @Bean
    public StepExecutionListener snailListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(StepExecution stepExecution) {
                try {
                    //Make sluggish job for the restart testing
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            @Override
            public ExitStatus afterStep(StepExecution stepExecution) {
                return null;  //To change body of implemented methods use File | Settings | File Templates.
            }
        };
    }

    @Bean
    public StaxEventItemReader xmlItemReader() throws MalformedURLException {
        StaxEventItemReader itemReader = new StaxEventItemReader();
        itemReader.setFragmentRootElementName("station");
        itemReader.setResource(new ClassPathResource("/data/input/abc-local-radio.xml"));
        itemReader.setUnmarshaller(stationMarshaller());
        return itemReader;
    }

    @Bean
    public Jaxb2Marshaller stationMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(StationType.class);
        //make sluggish job for resume and restart job testing
        return marshaller;
    }

    @Bean
    public FlatFileItemWriter xmlItemWriter() {
        FlatFileItemWriter<StationType> itemWriter = new FlatFileItemWriter<StationType>();
        itemWriter.setResource(new FileSystemResource("./output/abc-local-radio.csv"));
        itemWriter.setShouldDeleteIfExists(true);
        itemWriter.setLineAggregator(delimitedLineAggregator());
        return itemWriter;
    }

    private LineAggregator<StationType> delimitedLineAggregator() {
        DelimitedLineAggregator lineAggregator = new DelimitedLineAggregator();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(beanWrapperFieldExtractor());
        return lineAggregator;
    }

    private FieldExtractor beanWrapperFieldExtractor() {
        BeanWrapperFieldExtractor fieldExtractor = new BeanWrapperFieldExtractor();
        fieldExtractor.setNames(new String[] { "town", "stationname", "websiteUrl", "talkbackNumber"});
        return fieldExtractor;
    }


}
