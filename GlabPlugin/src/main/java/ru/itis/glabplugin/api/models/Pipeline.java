package ru.itis.glabplugin.api.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class Pipeline extends AbstractModel {

    private Integer id;
    @JsonProperty("project_id")
    private Integer projectId;
    private String status;
    private String source;

    @JsonProperty("ref")
    private String branchName;

    @JsonProperty("sha")
    private String commit;
    private String createdAt;
    private String updatedAt;
    private String webUrl;
    private Double duration;
}
