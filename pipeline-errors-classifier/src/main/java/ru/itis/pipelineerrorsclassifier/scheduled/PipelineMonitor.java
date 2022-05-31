package ru.itis.pipelineerrorsclassifier.scheduled;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.itis.pipelineerrorsclassifier.api.GitlabAPI;
import ru.itis.pipelineerrorsclassifier.api.GitlabService;
import ru.itis.pipelineerrorsclassifier.api.RestClient;
import ru.itis.pipelineerrorsclassifier.api.TemplateGenerator;
import ru.itis.pipelineerrorsclassifier.dto.Template;
import ru.itis.pipelineerrorsclassifier.models.*;
import ru.itis.pipelineerrorsclassifier.models.Error;
import ru.itis.pipelineerrorsclassifier.repositories.ConfigRepository;
import ru.itis.pipelineerrorsclassifier.repositories.PipelineRepository;
import ru.itis.pipelineerrorsclassifier.repositories.ProjectRepository;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.channels.Pipe;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 27.05.2022
 *
 * @author Azat Yamanaev
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PipelineMonitor {

    private static final long[] projects = new long[]{36471482L, 33346042L};
    private final GitlabAPI gitlabAPI;
    private final GitlabService gitlabService;
    private final ProjectRepository projectRepository;
    private final PipelineRepository pipelineRepository;
    private final TemplateGenerator templateGenerator;



//    @Scheduled(cron = "0 0/5 * * * *")
    public void updatePipelines() {
        log.info("updating pipelines");

        for (long id : projects) {
            Project project = projectRepository.findById(id).orElse(null);
            if (project != null) {
                List<Pipeline> pipelines = gitlabAPI.getProjectPipelines(id, 1, 50);
                if (pipelines != null && !pipelines.isEmpty()) {
                    String maxDate = pipelineRepository.lastCreated(id);
                    pipelines.stream().filter(pipeline -> pipeline.getCreatedAt().compareTo(maxDate) > 0)
                            .filter(pipeline -> !pipeline.getStatus().equals("running"))
                            .forEach(pipeline -> {
                                Pipeline saved = pipelineRepository.save(pipeline);
                                gitlabService.updatePipelineJobs(pipeline.getId());
                                gitlabService.updatePipelineDiff(pipeline.getId());
                                saved = pipelineRepository.findById(saved.getId()).orElse(null);
                                if (pipeline.getStatus().equals("failed")) {
                                    Config config = templateGenerator.generateConfig(id);
                                }
                            });
                }
            }
        }
    }



}
