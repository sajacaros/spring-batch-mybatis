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
    void batchTable() throws Exception;

    void batchColumn() throws Exception;

    void batchPrimaryKey() throws Exception;

    @Service
    @Slf4j
    class Default implements BatchService {
        @Autowired
        private JobLauncher jobLauncher;


        @Autowired
        @Qualifier("tableMetadataJob")
        private Job tableJob;

        @Autowired
        @Qualifier("columnMetadataJob")
        private Job columnJob;

        @Autowired
        @Qualifier("primaryKeyMetadataJob")
        private Job primaryKeyJob;

        @Override
        public void batchTable() throws Exception {
            JobExecution run = jobLauncher.run(
                    tableJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters()
            );
            log.info("sync] table job id : {}, state : {}", run.getJobId(), run.getExitStatus());
        }

        @Override
        public void batchColumn() throws Exception {
            JobExecution run = jobLauncher.run(
                    columnJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters()
            );
            log.info("sync] column job id : {}, state : {}", run.getJobId(), run.getExitStatus());
        }

        @Override
        public void batchPrimaryKey() throws Exception {
            JobExecution run = jobLauncher.run(
                    primaryKeyJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters()
            );
            log.info("sync] primary key job id : {}, state : {}", run.getJobId(), run.getExitStatus());
        }
    }
}
