package com.studio.scout.controller;

import com.studio.scout.model.AppliedJob;
import com.studio.scout.repository.AppliedJobRepository;
import com.studio.scout.service.AutomatedScoutEngine;
import com.studio.scout.service.ThrottledMailerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/pipeline")
@CrossOrigin(origins = "*") // Allows your static GitHub Dashboard UI to parse metrics freely
public class CronGatewayController {

    private final AutomatedScoutEngine scoutEngine;
    private final ThrottledMailerService mailerService;
    private final AppliedJobRepository repository;

    public CronGatewayController(AutomatedScoutEngine scoutEngine,
                                 ThrottledMailerService mailerService,
                                 AppliedJobRepository repository) {
        this.scoutEngine = scoutEngine;
        this.mailerService = mailerService;
        this.repository = repository;
    }

    // Activated daily by Cron-Job.org at 09:05 AM [cite: 14]
    @GetMapping("/trigger")
    public ResponseEntity<String> executePipelineTrigger() {
        scoutEngine.runMorningScan();
        mailerService.processQueueAndSend();
        return ResponseEntity.ok("Pipeline processing triggered safely in background thread.");
    }

    // Serves real-time analytical logs to your frontend tracking dashboard
    @GetMapping("/logs")
    public ResponseEntity<List<AppliedJob>> getAllApplicationLogs() {
        return ResponseEntity.ok(repository.findAllByOrderBySourcedDateDesc());
    }
}