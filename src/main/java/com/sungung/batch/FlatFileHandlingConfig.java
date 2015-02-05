package com.sungung.batch;

import com.sungung.data.model.Brewer;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.math.BigDecimal;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Configuration
public class FlatFileHandlingConfig {

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job fileJob(Step fileJobStep) {
        return jobBuilderFactory
                .get("fileJob")
                .start(fileJobStep)
                .build();
    }

    @Bean
    public Step fileJobStep(ItemReader fileItemReader, ItemWriter fileItemWriter) {
        return stepBuilderFactory
                .get("fileJobStep")
                .chunk(2)
                .reader(fileItemReader)
                .processor(itemProcessor())
                .writer(fileItemWriter)
                .build();
    }

    @Bean
    public ItemProcessor itemProcessor() {
        return new ItemProcessor<Brewer, Brewer>() {
            @Override
            public Brewer process(Brewer item) throws Exception {
                item.setCredit(item.getCredit().multiply(BigDecimal.valueOf(2)));
                return item;
            }
        };  //To change body of created methods use File | Settings | File Templates.
    }


    @Bean
    public DelimitedLineTokenizer delimitedLineTokenizer() {
        DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
        delimitedLineTokenizer.setDelimiter(",");
        delimitedLineTokenizer.setNames(new String[]{"id", "name","email", "credit"});
        return delimitedLineTokenizer;
    }

    @Bean
    public BeanWrapperFieldSetMapper beanWrapperFieldSetMapper() {
        BeanWrapperFieldSetMapper beanWrapperFieldSetMapper = new BeanWrapperFieldSetMapper();
        beanWrapperFieldSetMapper.setTargetType(Brewer.class);
        return beanWrapperFieldSetMapper;
    }

    @Bean
    public DefaultLineMapper defaultLineMapper() {
        DefaultLineMapper lineMapper = new DefaultLineMapper();
        lineMapper.setLineTokenizer(delimitedLineTokenizer());
        lineMapper.setFieldSetMapper(beanWrapperFieldSetMapper());
        return lineMapper;
    }

    @Bean
    public DelimitedLineAggregator delimitedLineAggregator() {
        DelimitedLineAggregator delimitedLineAggregator = new DelimitedLineAggregator();
        delimitedLineAggregator.setDelimiter(",");
        delimitedLineAggregator.setFieldExtractor(beanWrapperFieldExtractor());
        return delimitedLineAggregator;
    }

    @Bean
    public BeanWrapperFieldExtractor beanWrapperFieldExtractor() {
        BeanWrapperFieldExtractor beanWrapperFieldExtractor = new BeanWrapperFieldExtractor();
        beanWrapperFieldExtractor.setNames(new String[]{"id","name","email","credit"});
        return beanWrapperFieldExtractor;
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Brewer> fileItemReader(@Value("#{jobParameters[inputFile]}") Resource inputFile) {
        FlatFileItemReader itemReader = new FlatFileItemReader();
        itemReader.setLineMapper(defaultLineMapper());
        itemReader.setResource(inputFile);
        return itemReader;
    }

    @Bean
    @StepScope
    public FlatFileItemWriter<Brewer> fileItemWriter(@Value("#{jobParameters[outputFile]}") Resource outputFile) {
        FlatFileItemWriter itemWriter = new FlatFileItemWriter();
        itemWriter.setResource(outputFile);
        itemWriter.setLineAggregator(delimitedLineAggregator());
        return itemWriter;
    }




}
