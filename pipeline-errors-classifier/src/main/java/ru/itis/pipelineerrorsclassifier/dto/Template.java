package ru.itis.pipelineerrorsclassifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.itis.pipelineerrorsclassifier.models.Config;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 30.05.2022
 *
 * @author Azat Yamanaev
 */
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Template {

    private Config config;
    private Map<String, Entry> entries;


    @Data
    @SuperBuilder
    public static class Entry {

        private String string;
        private double count;
        private double all;
        private String command;
        private Set<Integer> number;


        public double getFrequency() {
            return  count / all;
        }
    }
}
