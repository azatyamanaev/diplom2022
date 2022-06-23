package ru.itis.classifier.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.itis.classifier.api.GitlabAPI;
import ru.itis.classifier.api.Service;
import ru.itis.classifier.api.TemplateGenerator;

import java.io.FileWriter;
import java.io.IOException;

/**
 * 27.05.2022
 *
 * @author Azat Yamanaev
 */
@RequiredArgsConstructor
@RestController
@RequestMapping(CommonController.ROOT_URL)
@Slf4j
public class CommonController {

    public static final String ROOT_URL = "/common";
    private static final String ID = "/{id}";
    private static final String JOB_LOG = "/log";
    private static final String PROJECTS = "/projects";
    private static final String PIPELINES = "/pipelines";
    private static final String JOBS = "/jobs";
    private static final String COMMITS = "/commits";
    private static final String TEMPLATES = "/templates";

    private final GitlabAPI gitlabAPI;
    private final Service service;
    private final TemplateGenerator templateGenerator;


    @GetMapping("/file")
    public ResponseEntity<String> getFile(@RequestParam Long projectId, @RequestParam String path) {
        return ResponseEntity.ok(gitlabAPI.getFile(projectId, path));
    }

    @GetMapping("/file/yml")
    public ResponseEntity<String> getFileYml(@RequestParam Long projectId) {
        templateGenerator.generateConfig(projectId);
        return ResponseEntity.ok("OK");
    }

    @GetMapping("/status")
    public ResponseEntity<String> status() {
        return ResponseEntity.ok("OK");
    }


    @GetMapping(JOB_LOG)
    public ResponseEntity<String> getLog(@RequestParam String projectId, @RequestParam String jobId) throws IOException {
        String res = gitlabAPI.getJobLog(Long.parseLong(projectId), Long.parseLong(jobId));
        FileWriter writer = new FileWriter("./file.log");
        res = res.replace("\u001B", "")
                .replace("[0K", "")
                .replace("[36;1m", "")
                .replace("[32;1m", "")
                .replace("[31;1m", "")
                .replace("[0;m", "");
        int length = res.length();
        for (int i = 0; i < length / 100000+1; i++) {
            String part = res.substring(i*100000, Math.min(i*100000+100000, length));

            writer.write(part);
        }
        writer.close();
        log.info("length {}", res.length());

        return ResponseEntity.ok(res);
    }

    @PostMapping(PROJECTS)
    public ResponseEntity<String> updateProjects() {
        service.updateProjects();
        return ResponseEntity.ok("OK");
    }

    @PostMapping(PROJECTS + ID + PIPELINES)
    public ResponseEntity<String> updatePipelines(@PathVariable("id") Long projectId) {
        boolean res = service.updateProjectPipelines(projectId);
        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("project not found");
        }
    }

    @PostMapping(PROJECTS + ID + PIPELINES + JOBS)
    public ResponseEntity<String> updateProjectPipelinesJobs(@PathVariable("id") Long projectId) {
        boolean res = service.updateProjectPipelinesJobs(projectId);
        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("project not found");
        }
    }

    @PostMapping(PROJECTS + ID + PIPELINES + COMMITS)
    public ResponseEntity<String> updateProjectPipelinesDiffs(@PathVariable("id") Long projectId,
                                                              @RequestParam int from, @RequestParam int to) {
        boolean res = service.updateProjectPipelinesDiffs(projectId, from, to);
        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("project not found");
        }
    }



    @PostMapping(PIPELINES + ID + JOBS)
    public ResponseEntity<String> updatePipelineJobs(@PathVariable("id") Long pipelineId) {
        boolean res = service.updatePipelineJobs(pipelineId);
        if (res) {
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("pipeline not found");
        }
    }

    @PostMapping(PROJECTS + ID + TEMPLATES)
    public ResponseEntity<String> generateTemplates(@PathVariable("id") Long projectId) {
        service.generateTemplates(projectId);
        return ResponseEntity.ok("OK");
    }

}
