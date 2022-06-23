package ru.itis.classifier.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import ru.itis.classifier.models.template.Stage;

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

    private Stage stage;
    private Map<String, Entry> entries;


    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Entry {

        private String string;
        private double count;
        private double all;
        private String command;
        private Set<Integer> number;
        private double frequency;


        public double getFrequency() {
            this.frequency = count / all;
            return frequency;
        }
    }
}
