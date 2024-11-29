package kr.study.batch.controller;

import kr.study.batch.service.BatchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class BatchController {
    @Autowired
    BatchService batchService;

    @PostMapping("/batch/table")
    public void runTable() throws Exception {
        log.info("run table");
        batchService.batchTable();
    }

    @PostMapping("/batch/column")
    public void runColumn() throws Exception {
        log.info("run column");
        batchService.batchColumn();
    }

    @PostMapping("/batch/primary-key")
    public void runPrimaryKey() throws Exception {
        log.info("run primary key");
        batchService.batchPrimaryKey

                ();
    }
}
