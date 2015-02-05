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
import org.springframework.batch.item.database.*;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@Configuration
public class JdbcHandlingConfig {

    @Autowired
    DataSource dataSource;

    @Autowired
    JobBuilderFactory jobBuilderFactory;

    @Autowired
    StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job jdbcJob(Step jdbcStep) {
        return jobBuilderFactory
                .get("jdbcJob")
                .start(jdbcStep)
                .build();
    }

    @Bean
    public Step jdbcStep(ItemReader jdbcItemReader, ItemWriter jdbcItemWriter) {
        return stepBuilderFactory
                .get("jdbcStep")
                .chunk(2)
                .reader(jdbcItemReader)
                .processor(itemProcessor())
                .writer(jdbcItemWriter)
                .build();
    }

    private ItemProcessor itemProcessor() {
        return new ItemProcessor<Brewer, Brewer>() {
            @Override
            public Brewer process(Brewer item) throws Exception {
                item.setCredit(item.getCredit().multiply(BigDecimal.valueOf(2)));
                return item;
            }
        };  //To change body of created methods use File | Settings | File Templates.
    }

    @Bean
    protected PagingQueryProvider pagingQueryProvider() throws Exception {

        SqlPagingQueryProviderFactoryBean sqlPagingQueryProviderFactoryBean = new SqlPagingQueryProviderFactoryBean();
        sqlPagingQueryProviderFactoryBean.setDataSource(dataSource);
        sqlPagingQueryProviderFactoryBean.setSortKeys(new HashMap<String, Order>() {{
            put("id", Order.ASCENDING);
        }});
        sqlPagingQueryProviderFactoryBean.setSelectClause("select id, name, email, credit");
        sqlPagingQueryProviderFactoryBean.setFromClause("from brewer");
        sqlPagingQueryProviderFactoryBean.setWhereClause("where credit > :credit");
        return sqlPagingQueryProviderFactoryBean.getObject();
    }

    @Bean
    @StepScope
    public JdbcPagingItemReader<Brewer> jdbcItemReader(@Value("#{jobParameters[credit]}") final Double credit) throws Exception {

        JdbcPagingItemReader itemReader = new JdbcPagingItemReader();
        itemReader.setDataSource(dataSource);
        itemReader.setRowMapper(new RowMapper<Brewer>() {
            @Override
            public Brewer mapRow(ResultSet resultSet, int rowNum) throws SQLException {
                Brewer brewer = new Brewer();
                brewer.setId(resultSet.getLong("id"));
                brewer.setName(resultSet.getString("name"));
                brewer.setEmail(resultSet.getString("email"));
                brewer.setCredit(resultSet.getBigDecimal("credit"));
                return brewer;
            }
        });
        itemReader.setQueryProvider(pagingQueryProvider());
        itemReader.setPageSize(2);
        itemReader.setParameterValues(new HashMap<String, Object>() {{
            put("statusCode", "PE");
            put("credit", credit);
            put("type", "COLLECTION");
        }});

        return itemReader;
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Brewer> jdbcItemWriter() {

        JdbcBatchItemWriter itemWriter = new JdbcBatchItemWriter();
        itemWriter.setAssertUpdates(true);
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
        itemWriter.setSql("update brewer set credit = :credit where id = :id");
        itemWriter.setDataSource(dataSource);
        return itemWriter;
    }
}
