package ru.itis.classifier.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import ru.itis.classifier.dto.ErrorDto;
import ru.itis.classifier.dto.Template;
import ru.itis.classifier.models.*;
import ru.itis.classifier.models.template.Config;
import ru.itis.classifier.models.template.Error;
import ru.itis.classifier.models.template.Stage;
import ru.itis.classifier.repositories.*;
import ru.itis.classifier.utils.Settings;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
@org.springframework.stereotype.Service
@RequiredArgsConstructor
@Slf4j
public class Service {

    private static final int PER_PAGE = 50;
    private final GitlabAPI gitlabAPI;

    private final ProjectRepository projectRepository;
    private final PipelineRepository pipelineRepository;
    private final PipelineJobRepository pipelineJobRepository;
    private final CommitRepository commitRepository;
    private final DiffRepository diffRepository;
    private final ConfigRepository configRepository;
    private final StageRepository stageRepository;
    private final ErrorRepository errorRepository;

    private final TemplateGenerator generator;
    private final TemplateRepository repository;

    private final ObjectMapper mapper;

    private final Settings settings;


    public String writeLog(String projectName, Long projectId, Long pipelineId, Long jobId, String stage) {
        try {
            String logData = gitlabAPI.getJobLog(projectId, jobId);
            if (logData != null) {
                String path = settings.getLogsDir() + File.separator + projectName + File.separator + pipelineId;

                File file = new File(path);
                file.mkdirs();
                FileWriter writer = new FileWriter(path + File.separator + stage + ".log");
                int length = logData.length();
                for (int i = 0; i < length / 100000 + 1; i++) {
                    String part = logData.substring(i * 100000, Math.min(i * 100000 + 100000, length));
                    writer.write(part);
                }
                writer.close();
                return path;
            } else {
                return null;
            }
        } catch (IOException e) {
            log.error("Error while saving log to file");
        }
        return null;
    }


    public void updateProjects() {
        int page = 1;

        List<Project> projects = gitlabAPI.getUserProjects(page, PER_PAGE);

        while (projects != null && !projects.isEmpty()) {
            projectRepository.saveAll(projects);
            page++;
            projects = gitlabAPI.getUserProjects(page, PER_PAGE);
        }

    }


    public boolean updateProjectPipelines(Long projectId) {
        Project project = checkProject(projectId);
        if (project == null) return false;
        int page = 1;
        List<Pipeline> pipelines = gitlabAPI.getProjectPipelines(projectId, page, PER_PAGE);
        while (pipelines != null && !pipelines.isEmpty()) {
            pipelines.forEach(pipeline -> {
                pipeline.setProject(project);
                pipelineRepository.save(pipeline);
            });
            page++;
            pipelines = gitlabAPI.getProjectPipelines(projectId, page, PER_PAGE);
        }

        return true;
    }


    public boolean updateProjectPipelinesJobs(Long projectId) {
        Project project = checkProject(projectId);
        if (project == null) return false;
        List<Pipeline> pipelines = project.getPipelines();
        if (pipelines != null && pipelines.size() > 0) {
            pipelines.stream().filter(pipeline -> pipeline.getId() > 547600126L)
                    .forEach(pipeline -> {
                        updatePipelineJobs(pipeline.getId());
                    });
        }
        return true;
    }

    @Transactional
    public boolean updateProjectPipelinesDiffs(Long projectId, int from, int to) {
        Project project = checkProject(projectId);
        if (project == null) return false;
        List<Pipeline> pipelines = project.getPipelines();
        if (pipelines != null && pipelines.size() > 0) {
            for (int i = from; i < Math.min(to, pipelines.size()); i++) {
                updatePipelineDiff(pipelines.get(i).getId());
            }
        }
        return true;
    }

    private Project checkProject(Long projectId) {
        Project project = projectRepository.findById(projectId).orElse(null);
        if (project == null) {
            project = gitlabAPI.getProject(projectId);
            if (project == null) return null;
            projectRepository.save(project);
        }
        return project;
    }

    public boolean updatePipelineJobs(Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElse(null);
        if (pipeline == null) return false;

        Long projectId = pipeline.getProject().getId();
        pipelineJobRepository.deleteAll(pipelineJobRepository.findAllByPipeline_Id(pipelineId));
        List<PipelineJob> jobs = gitlabAPI.getPipelineJobs(projectId, pipelineId);
        if (jobs != null) {
            jobs.stream().forEach(job -> {
                String path = writeLog(pipeline.getProject().getName(), projectId, pipelineId, job.getId(), job.getStage());
                job.setLogPath(path);
                job.setPipeline(pipeline);
                pipelineJobRepository.save(job);
            });
        }
        return true;
    }

    @Transactional
    public boolean updatePipelineDiff(Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElse(null);
        if (pipeline == null) return false;

        Long projectId = pipeline.getProject().getId();
        String sha = pipeline.getCommit();
        Commit commit = gitlabAPI.getCommit(projectId, sha);
        if (commit != null) {
            commitRepository.findByPid(sha).ifPresent(exCom -> commit.setId(exCom.getId()));
            commitRepository.save(commit);
            List<Diff> diffs = gitlabAPI.getCommitDiff(projectId, sha);

            if (diffs != null && diffs.size() > 0) {
                diffRepository.deleteByCID(sha);
                diffs.forEach(diff -> {
                    diff.setCommit(commit);
                    diffRepository.save(diff);
                });
            }
            return true;
        }
        return false;
    }


    public void generateTemplates(Long projectId) {
        Long id1 = 519489475L;
        Long id2 = 547600126L;
        Long id3 = 565328374L;
        List<Pipeline> pipelines = pipelineRepository.findAllByIdGreaterThanAndProject_IdAndStatus(id3, projectId, "success");
        log.info("found {}", pipelines.size());
        Config config = configRepository.findById(1L).orElse(null);
        Map<Stage, Template> map = generator.getTemplates(pipelines, config);

        log.info("map {}", map);

        if (map.size() > 0) {
            map.keySet().forEach(stage -> {
                ru.itis.classifier.models.template.Template template = ru.itis.classifier.models.template.Template.builder()
                        .id(repository.findMaxId())
                        .stageName(stage.getName())
                        .stage(stage)
                        .template(mapper.valueToTree(map.get(stage).getEntries()))
                        .build();

                repository.save(template);
            });
        }

    }


    public ErrorDto getError(Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId).orElse(null);
        if (pipeline != null && pipeline.getError() != null) {
            Error error = pipeline.getError();
            return ErrorDto.builder()
                    .stage(error.getStage())
                    .command(error.getAfterCommand())
                    .log(error.getLog())
                    .type(error.getType().toString())
                    .build();
        } else {
            return null;
        }
    }

    public List<String> getTemplate(String name) {
        List<ru.itis.classifier.models.template.Template> templates = repository.findAllByStageName(name);

        JsonNode node = templates.get(0).getTemplate();
        Template template = Template.builder()
                .entries(mapper.convertValue(node, new TypeReference<>() {
                }))
                .build();
        return new ArrayList<>(template.getEntries().keySet());
    }


    public ErrorDto determineError(Long pipelineId) {
        List<PipelineJob> jobs = pipelineJobRepository.findAllByPipeline_Id(pipelineId);
        PipelineJob job = null;
        for (PipelineJob j : jobs) {
            if (j.getStatus().equals("failed")) {
                job = j;
            }
        }
        if (job != null) {

            ru.itis.classifier.models.template.Template template = repository.findAllByStageName(job.getStage()).get(0);
            Template dto = Template.builder()
                    .stage(stageRepository.findAllByName(job.getStage()).get(0))
                    .entries(mapper.convertValue(template.getTemplate(), new TypeReference<>() {
                    }))
                    .build();
            Error error = generator.determineError(job, dto.getStage(), dto);
            error.setConfig(configRepository.findById(1L).get());
            error.setId(errorRepository.findMaxId());
            Long eid = errorRepository.save(error).getId();
            pipelineRepository.update(eid, pipelineId);
            return ErrorDto.builder()
                    .stage(error.getStage())
                    .command(error.getAfterCommand())
                    .log(error.getLog())
                    .type(error.getType().toString())
                    .build();
        } else {
            return null;
        }
    }

}
