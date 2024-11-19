package kr.study.batch.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

public interface BatchService {
    void batch() throws Exception;

    @Service
    @Slf4j
    class Default implements BatchService {
        @Autowired
        private JobLauncher jobLauncher;


        @Autowired
        @Qualifier("tableMetadataJob")
        private Job job;

        @Override
        public void batch() throws Exception {
            JobExecution run = jobLauncher.run(
                    job,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters()
            );
            log.info("sync] job id : {}, state : {}", run.getJobId(), run.getExitStatus());
        }
    }
}
