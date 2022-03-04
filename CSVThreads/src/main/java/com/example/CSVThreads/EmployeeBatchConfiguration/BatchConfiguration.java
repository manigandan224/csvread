package com.example.CSVThreads.EmployeeBatchConfiguration;
import com.example.CSVThreads.CSVModel.ThreadModel;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;

@EnableBatchProcessing
@Configuration
public class BatchConfiguration {

    @Bean
    public Job job(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory, ItemReader<ThreadModel> itemReader, ItemProcessor<ThreadModel,ThreadModel> itemProcessor, ItemWriter<ThreadModel> itemWriter)
    {
        Step step=stepBuilderFactory.get("ETL-file=load").<ThreadModel,ThreadModel>chunk(100).reader(itemReader).processor(itemProcessor).writer(itemWriter).build();
        return jobBuilderFactory.get("ETL-Load").incrementer(new RunIdIncrementer()).start(step).build();
    }
    @Bean
    public FlatFileItemReader<ThreadModel> itemReader(@Value("${input}") Resource resources)
    {
        FlatFileItemReader<ThreadModel> multiResourceItemReader=new FlatFileItemReader<>();
        multiResourceItemReader.setResource(resources);
        multiResourceItemReader.setLinesToSkip(1);
        multiResourceItemReader.setLineMapper(lineMapper());
        return multiResourceItemReader;
    }
    @Bean
    public LineMapper<ThreadModel> lineMapper(){
        DefaultLineMapper<ThreadModel> defaultLineMapper=new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer=new DelimitedLineTokenizer();
        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames(new String[]{"Id","Name","Address","Email"});
        BeanWrapperFieldSetMapper<ThreadModel> fieldSetMapper=new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(ThreadModel.class);
        defaultLineMapper.setLineTokenizer(lineTokenizer);
        defaultLineMapper.setFieldSetMapper(fieldSetMapper);
        return defaultLineMapper;
    }
}
