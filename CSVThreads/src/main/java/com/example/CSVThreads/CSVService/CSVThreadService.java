package com.example.CSVThreads.CSVService;

import com.example.CSVThreads.CSVModel.ThreadModel;
import com.example.CSVThreads.CSVRepository.CSVThreadRepository;
import com.univocity.parsers.common.record.Record;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
@Service
public class CSVThreadService {
    private static final Logger LOGGER= LoggerFactory.getLogger(CSVThreadService.class);
    @Autowired
    private CSVThreadRepository repository;
    // Method to asynchronous thread processing
    @Async
    public CompletableFuture<List<ThreadModel>> saveModels(final InputStream inputStream) throws Exception{
        final long start=System.currentTimeMillis();
        List<ThreadModel> threads = parseCSVFile(inputStream);
        LOGGER.info("Saving a list of threads of size {} records",threads.size());
        threads=repository.saveAll(threads);
        LOGGER.info("Elapsed time: {}",(System.currentTimeMillis()-start));
        return CompletableFuture.completedFuture(threads);
    }
    // Method to parseCSVFile
    private List<ThreadModel> parseCSVFile(final InputStream inputStream) throws Exception{
        final List<ThreadModel> threads=new ArrayList<>();
        try {
            CsvParserSettings setting = new CsvParserSettings();
            setting.setHeaderExtractionEnabled(true);
            CsvParser parser = new CsvParser(setting);
            List<Record> parseAllRecords = parser.parseAllRecords(inputStream);
            parseAllRecords.forEach(record -> {
                final ThreadModel thread=new ThreadModel();
                thread.setName(record.getString("name"));
                thread.setAddress(record.getString("address"));
                thread.setEmail(record.getString("email"));
                LOGGER.info(String.valueOf(thread));
                threads.add(thread);
            });
            return threads;
        }
        catch(Exception e)
        {
                LOGGER.error("Failed to parse csv file {}",e);
                throw new Exception("Failed to parse csv file{}",e);
        }
    }
    // Method to getting all the employee records
    @Async
    public CompletableFuture<List<ThreadModel>> getAllModels(){
        LOGGER.info("Request to get a list of models");
        final List<ThreadModel> threads=repository.findAll();
        return CompletableFuture.completedFuture(threads);
    }
}

