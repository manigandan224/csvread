package com.example.CSVThreads.Batch;

import com.example.CSVThreads.CSVModel.ThreadModel;
import com.example.CSVThreads.CSVRepository.CSVThreadRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DBWriter implements ItemWriter<ThreadModel> {
    @Autowired
    CSVThreadRepository repository;
    @Override
    public void write(List<? extends ThreadModel> thread) throws Exception{
        System.out.println("Data Saved for Users:"+thread);
        repository.saveAll(thread);
    }
}
