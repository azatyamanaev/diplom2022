package ru.itis.classifier.scheduled;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.itis.classifier.api.GitlabAPI;
import ru.itis.classifier.api.GitlabService;
import ru.itis.classifier.api.TemplateGenerator;
import ru.itis.classifier.models.*;
import ru.itis.classifier.repositories.PipelineRepository;
import ru.itis.classifier.repositories.ProjectRepository;

import java.util.List;

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
