package com.example.CSVThreads.CSVController;
import com.example.CSVThreads.CSVException.ResourceNotFoundException;
import com.example.CSVThreads.CSVModel.ThreadModel;
import com.example.CSVThreads.CSVRepository.CSVThreadRepository;
import com.example.CSVThreads.CSVService.CSVThreadService;
import com.example.CSVThreads.EmployeeBatchConfiguration.BatchConfiguration;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.FilenameUtils;
import org.springframework.batch.core.*;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

@EnableScheduling
@RestController
@RequestMapping("/api/thread")
public class CSVThreadController {
    private static final Logger LOGGER= LoggerFactory.getLogger(CSVThreadController.class);
    @Autowired
    private CSVThreadService service;
    @Autowired
    private CSVThreadRepository repository;
    @Autowired
    private Job importUserJob;
    @Autowired
    private JobLauncher jobLauncher;
    //Method to upload files
    @RequestMapping(method=RequestMethod.POST,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},produces={MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody ResponseEntity uploadFile(@RequestParam(value="file") MultipartFile[] files) throws Exception
    {
        try{
            for(final MultipartFile file:files)
            {
                String path = new ClassPathResource("/").getURL().getPath();//it's assumed you have a folder called tmpuploads in the resources folder
                File fileToImport = new File(path + file.getOriginalFilename());
                String ext = FilenameUtils.getExtension(file.getOriginalFilename());
                OutputStream outputStream = new FileOutputStream(fileToImport);
                IOUtils.copy(file.getInputStream(), outputStream);
                outputStream.flush();
                outputStream.close();

                //Launch the Batch Job
//                JobExecution jobExecution = jobLauncher.run(importUserJob, new JobParametersBuilder()
//                        .addString("fullPathFileName", fileToImport.getAbsolutePath())
//                        .toJobParameters());
                if (ext == "") {
                    return new ResponseEntity<>("Please attach csv file...", HttpStatus.NOT_ACCEPTABLE);
                }
                service.saveModels(file.getInputStream());
            }
            return new ResponseEntity("Inserted Successfully",HttpStatus.CREATED);
        }
        catch(final Exception e)
        {
            return new ResponseEntity("Not Inserted",HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    //Meethod to getting the records
    @RequestMapping(method=RequestMethod.GET,consumes = {MediaType.MULTIPART_FORM_DATA_VALUE},produces={MediaType.APPLICATION_JSON_VALUE})
    public @ResponseBody CompletableFuture<ResponseEntity> getModels()
    {
        return service.getAllModels().<ResponseEntity>thenApply(ResponseEntity::ok).exceptionally(handleGetModelFailure);
    }
    private static Function<Throwable,ResponseEntity<? extends List<ThreadModel>>> handleGetModelFailure= throwable->{
        LOGGER.error("Failed to read records:{}",throwable);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    };
    @Autowired
    Job job;
    // Spring batch Execution
    @GetMapping("/load")
    public BatchStatus load() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        Map<String, JobParameter> maps=new HashMap<>();
        maps.put("time",new JobParameter(System.currentTimeMillis()));
        JobParameters parameters=new JobParameters(maps);
        JobExecution jobExecution=jobLauncher.run(job,parameters);
        System.out.println("JobExecution "+jobExecution.getStatus());
        System.out.println("Batch is Running");
        while(jobExecution.isRunning()){
            System.out.println("....");
        }
        return jobExecution.getStatus();
    }
    //Method to getting the single employee specified id
    @GetMapping("/employees/{id}")
    public ThreadModel getUserById(@PathVariable (value="id") long userId)
    {
        return repository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("user not found with id:"+userId));
    }
    //Method to deleting single employee specified id
    @DeleteMapping("/user/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id)
    {
        try {
            repository.deleteById(id);
            return new ResponseEntity<String>("deleted successfully",HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<String>("No data",HttpStatus.NOT_FOUND);
        }
    }
    //Method to update the employee
    @PutMapping("/updateemployee")
    public ResponseEntity<String> update(@RequestBody ThreadModel model)
    {
        try{
            saveOrUpdate(model);
            return new ResponseEntity<String>("Updated Successfully",HttpStatus.OK);
        }
        catch(Exception e)
        {
            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
        }
    }
    public void saveOrUpdate(ThreadModel model)
    {
        repository.save(model);
    }
}
