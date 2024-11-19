package kr.study.batch.controller;

import kr.study.batch.service.BatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatchController {
    @Autowired
    BatchService batchService;

    @PostMapping("/batch/run")
    public void run() throws Exception {
        batchService.batch();
    }
}
