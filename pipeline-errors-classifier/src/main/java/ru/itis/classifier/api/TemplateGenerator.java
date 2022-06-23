package ru.itis.classifier.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.classifier.dto.Template;
import ru.itis.classifier.models.*;
import ru.itis.classifier.models.template.Config;
import ru.itis.classifier.models.template.Error;
import ru.itis.classifier.models.template.Stage;
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

    private static final String build = "Hibernate: ";
    private static final String publish = " ---> ";
    private static final String deploy = "Pulling api ... ";


    public Map<Stage, Template> getTemplates(List<Pipeline> pipelines, Config config) {
        Map<String, List<PipelineJob>> jobs = new HashMap<>();
        pipelines.forEach(pipeline -> {
            pipeline.getJobs().forEach(job -> {
                if (!jobs.containsKey(job.getStage())) {
                    jobs.put(job.getStage(), List.of(job));
                } else {
                    List<PipelineJob> jobList = new ArrayList<>(jobs.get(job.getStage()));
                    jobList.add(job);
                    jobs.put(job.getStage(), jobList);
                }

            });
        });
        Map<Stage, Template> dict = new HashMap<>();
        List<Stage> stages = config.getStages();
        jobs.remove("test");
        jobs.forEach((key, value) -> {
            for (Stage stage : stages) {
                if (stage.getName().equals(key)) {
                    log.info("stage {}", stage.getName());
                    dict.put(stage, generateTemplate(value, stage));
                }
            }
        });
        return dict;
    }


    public Template generateTemplate(List<PipelineJob> jobs, Stage stage) {

        Map<String, Template.Entry> entries = new HashMap<>();
        Integer all = 0;
        for (PipelineJob job : jobs) {
            if (job.getLogPath() != null) {
                Set<Integer> nums;
                try {
                    String path = job.getLogPath() + "/" + job.getStage() + ".log";
                    BufferedReader reader = Files.newBufferedReader(Path.of(path));
                    String line = reader.readLine();
                    String[] cmds = stage.getCommands().split(",");
                    int ind = 0;
                    String current = null;
                    int lineNum = 1;
                    log.info("job {} with id {}, log {}", job.getStage(), job.getId(), job.getLogPath());

                    while (line != null) {
                        if (line.isBlank()) {
                            line = reader.readLine();
                            continue;
                        }
                        if (line.contains("section_start:") || line.contains("section_end:")) {
                            line = reader.readLine();
                            continue;
                        }
                        switch (job.getStage()) {
                            case "build":
                                if (line.contains(build)) {
                                    line = reader.readLine();
                                    continue;
                                }
                                break;
                            case "publish":
                                if (line.contains(publish)) {
                                    line = reader.readLine();
                                    continue;
                                }
                                break;
                            case "deploy":
                                if (line.contains(deploy)) {
                                    line = reader.readLine();
                                    continue;
                                }
                                break;
                        }
                        if (line.substring(2).equals(cmds[ind])) {
                            current = cmds[ind];
                            if (ind < cmds.length - 1) {
                                ind++;
                            }
                            line = reader.readLine();
                        }
                        if (current != null) {
                            String compare = compare(line, entries.keySet());
                            if (entries.containsKey(line)) {
                                Template.Entry entry = entries.get(line);
                                entry.setCount(entry.getCount() + 1);
                                nums = entry.getNumber();
                                nums.add(lineNum);
                                entry.setNumber(nums);
                                entries.put(line, entry);

                            } else if (compare != null) {
                                Template.Entry entry = entries.get(compare);
                                entry.setCount(entry.getCount() + 1);
                                nums = entry.getNumber();
                                nums.add(lineNum);
                                entry.setNumber(nums);
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

                    all += lineNum;


                } catch (IOException e) {
                    log.error("error while reading log file");
                }
            }
        }

        for (Template.Entry entry : entries.values()) {
            entry.setAll(all);
        }

        return Template.builder()
                .entries(entries)
                .stage(stage)
                .build();
    }

    public static String compare(String line, Set<String> set) {
        for (String key : set) {
            if (overlap(line, key) > 0.70) {
                return key;
            } else {
                List<String> s = List.of(line.split(" "));
                List<String> q = List.of(key.split(" "));
                if (s.size() == q.size()) {
                    int count = 0;
                    for (String ss : s) {
                        if (q.contains(ss)) {
                            count++;
                        }
                    }
                    if (count == q.size() - 1 || (count == q.size() - 2) && line.contains("Apache Maven")) {
                        return key;
                    }
                }

            }
        }
        return null;
    }

    public static double overlap(String s1, String s2) {
        if (s1.isBlank() || s2.isBlank()) return 0;
        double dist = Utils.calculate(s1, s2);

        return 1 - Math.max(dist / s1.length(), dist / s2.length());
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

    public Error determineError(PipelineJob failed, Stage stage, Template template) {

        List<String> list = new ArrayList<>();
        String afterCommand = null;
        if (failed.getLogPath() != null) {
            try {
                String path = failed.getLogPath() + "/" + failed.getStage() + ".log";
                BufferedReader reader = Files.newBufferedReader(Path.of(path));
                String line = reader.readLine();
                int lineNum = 1;
                String[] cmds = stage.getCommands().split(",");
                int ind = 0;
                log.info("job {} with id {}, log {}", failed.getStage(), failed.getId(), failed.getLogPath());

                String current = null;
                while (line != null) {
                    log.info("line num {}", lineNum);
                    if (line.isBlank()) {
                        line = reader.readLine();
                        continue;
                    }
                    if (line.contains("section_start:") || line.contains("section_end:")) {
                        line = reader.readLine();
                        continue;
                    }
                    switch (stage.getName()) {
                        case "build":
                            if (line.contains(build) || line.contains("\tat ")) {
                                line = reader.readLine();
                                continue;
                            }
                            break;
                        case "publish":
                            if (line.contains(publish)) {
                                line = reader.readLine();
                                continue;
                            }
                            break;
                        case "deploy":
                            if (line.contains(deploy)) {
                                line = reader.readLine();
                                continue;
                            }
                            break;
                    }

                    if (line.substring(2).equals(cmds[ind])) {
                        if (list.size() > 0) {
                            break;
                        }
                        current = cmds[ind];
                        if (ind < cmds.length - 1) {
                            ind++;
                        }
                        line = reader.readLine();
                    }
                    if (current != null) {
                        if (line.contains("[ERROR] -> [Help 1]") ||
                                line.contains("Cleaning up project directory and file based variables") ||
                                line.contains("Running after script")) {
                            break;
                        }
                        String compare = compare(line, template.getEntries().keySet());
                        if (compare == null && !template.getEntries().containsKey(line)) {
                            if (stage.getName().equals("publish")) {
                                compare = compare(line, new HashSet<>(list));
                            }
                            if (compare == null) {
                                list.add(line);
                            }
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
        }


        StringBuilder builder = new StringBuilder();
        for (String ss : list) {
            builder.append(ss).append("\n");
        }
        String log = builder.toString();
        Error.Type type;
        if (log.contains("There are test failures")) {
            type = Error.Type.UNIT_TEST_ERROR;
        } else if (log.contains("500 Internal Server Error")) {
            type = Error.Type.THIRD_PARTY_SERVICE_ERROR;
        } else if (log.contains("Compilation failure")) {
            type = Error.Type.COMPILATION_ERROR;
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
