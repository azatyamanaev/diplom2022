package ru.itis.pipelineerrorsclassifier.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.itis.pipelineerrorsclassifier.dto.Template;
import ru.itis.pipelineerrorsclassifier.models.Config;
import ru.itis.pipelineerrorsclassifier.models.Pipeline;
import ru.itis.pipelineerrorsclassifier.models.PipelineJob;

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


    public Template generateTemplate(List<PipelineJob> jobs, Config config) {

        Map<String, Template.Entry> entries = new HashMap<>();
        for (PipelineJob job : jobs) {
            try {
                BufferedReader reader = Files.newBufferedReader(Path.of(job.getLogPath()));
                String line = reader.readLine();
                String[] cmds = config.getCommands().split(",");
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
                .config(config)
                .build();
    }

    public String compare(String line, Set<String> set) {
        for (String key : set) {
            if (overlap(line, key) > 0.75) {
                return key;
            }
        }
        return null;
    }

    public double overlap(String s1, String s2) {
        char[] c1 = s1.toCharArray();
        char[] c2 = s2.toCharArray();
        double cnt = 0;
        for (int i = 0; i < c1.length; i++) {
            if (c1[i] == c2[i]) {
                cnt++;
            } else {
                break;
            }
        }

        return Math.min(cnt / c1.length, cnt / c2.length);
    }
}
