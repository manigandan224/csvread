package com.example.CSVThreads.Batch;

import com.example.CSVThreads.CSVModel.ThreadModel;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class Processor implements ItemProcessor<ThreadModel,ThreadModel> {
    private static final Map<String,String> EMAILS=new HashMap<>();
    public Processor(){
        EMAILS.put("001","manigandan27052000@gmail.com");
        EMAILS.put("002","manivijay27052000@gmail.com");
        EMAILS.put("003","mani27052000@gmail.com");
    }
    public ThreadModel process(ThreadModel threads) throws Exception{
        String emailcode=threads.getEmail();
        String email=EMAILS.get(emailcode);
        threads.setEmail(email);
        threads.setTime(new Date());
        return threads;
    }
}
