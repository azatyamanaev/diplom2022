package ru.itis.classifier.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.classifier.dto.Template;
import ru.itis.classifier.models.*;
import ru.itis.classifier.models.Error;
import ru.itis.classifier.repositories.ConfigRepository;
import ru.itis.classifier.repositories.StageRepository;
import ru.itis.classifier.utils.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * 31.05.2022
 *
 * @author Azat Yamanaev
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateGenerator {

    private final GitlabAPI gitlabAPI;
    private final ObjectMapper ymlMapper;
    private final ConfigRepository configRepository;
    private final StageRepository stageRepository;


    public Map<Stage, Template> getTemplates(List<Pipeline> pipelines, Config config) {
        Map<String, List<PipelineJob>> jobs = new HashMap<>();
        pipelines.forEach(pipeline -> {
            pipeline.getJobs().forEach(job -> {
                if (!jobs.containsKey(job.getStage())) {
                    jobs.put(job.getStage(), List.of(job));
                } else {
                    List<PipelineJob> jobList = jobs.get(job.getStage());
                    jobList.add(job);
                    jobs.put(job.getStage(), jobList);
                }
            });
        });
        Map<Stage, Template> dict = new HashMap<>();
        List<Stage> stages = config.getStages();
        jobs.forEach((key, value) -> {
            for (Stage stage : stages) {
                if (stage.getName().equals(key)) {
                    dict.put(stage, generateTemplate(value, stage));
                    break;
                }
            }
        });
        return dict;
    }


    public Template generateTemplate(List<PipelineJob> jobs, Stage stage) {

        Map<String, Template.Entry> entries = new HashMap<>();
        for (PipelineJob job : jobs) {
            try {
                BufferedReader reader = Files.newBufferedReader(Path.of(job.getLogPath()));
                String line = reader.readLine();
                String[] cmds = stage.getCommands().split(",");
                int ind = 0;

                String current = null;
                int lineNum = 1;
                while (line != null) {
                    if (line.substring(2).equals(cmds[ind])) {
                        current = cmds[ind];
                        ind++;
                        line = reader.readLine();
                        lineNum++;
                        continue;
                    } else if (current != null) {
                        String compare = compare(line, entries.keySet());
                        if (entries.containsKey(line)) {
                            Template.Entry entry = entries.get(line);
                            entry.setCount(entry.getCount() + 1);
                            entry.setAll(entry.getAll() + 1);
                            if (entry.getNumber().contains(lineNum)) {
                                entry.getNumber().add(lineNum);
                            }
                            entries.put(line, entry);

                        } else if (compare != null) {
                            Template.Entry entry = entries.get(compare);
                            entry.setCount(entry.getCount() + 1);
                            entry.setAll(entry.getAll() + 1);
                            if (entry.getNumber().contains(lineNum)) {
                                entry.getNumber().add(lineNum);
                            }
                            entries.put(compare, entry);
                        } else {
                            entries.put(line, Template.Entry.builder()
                                    .all(1)
                                    .count(1)
                                    .string(line)
                                    .command(current)
                                    .number(new HashSet<>(lineNum))
                                    .build());
                        }
                    }

                    line = reader.readLine();
                    lineNum++;
                }


            } catch (IOException e) {
                log.error("error while reading log file");
            }
        }

        return Template.builder()
                .entries(entries)
                .stage(stage)
                .build();
    }

    public static String compare(String line, Set<String> set) {
        for (String key : set) {
            if (overlap(line, key) > 0.75) {
                return key;
            }
        }
        return null;
    }

    public static double overlap(String s1, String s2) {
        int dist = Utils.calculate(s1, s2);

        return Math.max(dist / s1.length(), dist / s2.length());
    }

    public Config generateConfig(Long projectId) {
        String text = gitlabAPI.getFile(projectId, ".gitlab-ci.yml");
        try {

            Map<String, Object> map = ymlMapper.readValue(text, Map.class);
            List<String> stagesList = (List<String>) map.get("stages");

            Config config = Config.builder()
                    .text(text)
                    .variables(ymlMapper.valueToTree(map.get("variables")))
                    .stagesList(String.valueOf(stagesList))
                    .build();

            List<Stage> stages = new ArrayList<>();
            stagesList.forEach(stage -> {
                StringBuilder builder = new StringBuilder();
                Map<String, Object> mm = (Map<String, Object>) map.get(stage);
                List<String> cmds = (List<String>) mm.get("before_script");
                if (cmds != null) cmds.forEach(cmd -> builder.append(cmd + ","));
                cmds = (List<String>) mm.get("script");
                if (cmds != null) cmds.forEach(cmd -> builder.append(cmd + ","));
                cmds = (List<String>) mm.get("after_script");
                if (cmds != null) cmds.forEach(cmd -> builder.append(cmd + ","));
                Stage stg = Stage.builder()
                        .name(stage)
                        .commands(builder.toString())
                        .config(config)
                        .build();
                if (!stageRepository.existsByCommandsAndName(stg.getCommands(), stg.getName())) {
                    stageRepository.save(stg);
                }
                stages.add(stg);
            });
            config.setStages(stages);
            return configRepository.save(config);
        } catch (JsonProcessingException e) {
            log.error("error when processing yml file");
            throw new RuntimeException(e);
        }
    }

    public Error determineError(Pipeline pipeline, Stage stage, Template template) {

        List<PipelineJob> jobs = pipeline.getJobs();
        PipelineJob failed = PipelineJob.builder().build();
        for (PipelineJob job : jobs) {
            if (job.getStatus().equals("failed")) {
                failed = job;
                break;
            }
        }
        StringBuilder builder = new StringBuilder();
        String afterCommand = null;
        try {
            BufferedReader reader = Files.newBufferedReader(Path.of(failed.getLogPath()));
            String line = reader.readLine();
            int lineNum = 1;
            String[] cmds = stage.getCommands().split(",");
            int ind = 0;

            String current = null;
            while (line != null) {
                if (line.substring(2).equals(cmds[ind])) {
                    current = cmds[ind];
                    ind++;
                    line = reader.readLine();
                    lineNum++;
                    continue;
                } else if (current != null) {
                    String compare = compare(line, template.getEntries().keySet());
                    if (compare == null && !template.getEntries().containsKey(line)) {
                        builder.append(line).append("\n");
                        afterCommand = current;
                    }
                }
                line = reader.readLine();
                lineNum++;
            }

        } catch (IOException e) {
            log.error("error while reading log file");
            throw new RuntimeException(e);
        }

        String log = builder.toString();
        Error.Type type;
        if (log.contains("There are test failures")) {
            type = Error.Type.UNIT_TEST_ERROR;
        } else if (log.contains("500 Internal Server Error")) {
            type = Error.Type.THIRD_PARTY_SERVICE_ERROR;
        } else {
            type = Error.Type.SCRIPT_FAILURE;
        }

        return Error.builder()
                .stage(failed.getStage())
                .type(type)
                .log(log)
                .afterCommand(afterCommand)
                .build();
    }
}
