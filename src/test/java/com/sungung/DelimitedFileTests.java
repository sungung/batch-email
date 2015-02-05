package com.sungung;

import com.sungung.data.model.Brewer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.test.MetaDataInstanceFactory;
import org.springframework.batch.test.StepScopeTestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.concurrent.Callable;

import static junit.framework.Assert.assertEquals;

/**
 * @author PARK Sungung
 * @since 0.0.1
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class})
public class DelimitedFileTests {

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    @Qualifier("fileJob")
    private Job job;

    @Autowired
    @Qualifier("fileItemReader")
    private ItemReader<Brewer> reader;

    @Autowired
    @Qualifier("fileItemReader")
    private ItemStream readerStream;

    @Test
    public void testLaunchJob() throws Exception {
        JobExecution jobExecution = jobLauncher.run(job, getJobParameters("/data/input/InputTwoBrewers.txt"));
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        //verifyOutput(2);

        jobExecution = jobLauncher.run(job, getJobParameters("/data/input/InputThreeBrewers.txt"));
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        //verifyOutput(3);

    }

    private void verifyOutput(int expected) throws Exception {

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("inputFile", "file:./output/OutputBrewer.txt")
                .toJobParameters();

        StepExecution stepExecution = MetaDataInstanceFactory.createStepExecution(jobParameters);

        int count = StepScopeTestUtils.doInStepScope(stepExecution, new Callable<Integer>() {

            @Override
            public Integer call() throws Exception {
                int count = 0;
                readerStream.open(new ExecutionContext());
                try {
                    while (reader.read() != null) {
                        count++;
                    }
                } finally {
                    readerStream.close();
                }
                return count;
            }
        });

        assertEquals(expected, count);

    }

    private JobParameters getJobParameters(String fileName) {
        return new JobParametersBuilder()
                .addLong("timestamp", new Date().getTime())
                .addString("inputFile", fileName)
                .addString("outputFile", "file:./output/OutputBrewer.txt")
                .toJobParameters();
    }
}
