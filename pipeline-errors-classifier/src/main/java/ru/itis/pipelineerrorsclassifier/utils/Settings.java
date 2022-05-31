package ru.itis.pipelineerrorsclassifier.utils;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotBlank;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "pipeline.monitor")
public class Settings {

    @NotBlank
    private String projectId;

    @NotBlank
    private String token;

    @NotBlank
    private String logsDir;
}
