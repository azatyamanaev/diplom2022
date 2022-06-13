package ru.itis.glabplugin.api.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class PipelineJob extends AbstractModel {

    private Long id;
    private String name;
    private String stage;
    private String status;
    private Double duration;
    private String webUrl;
    private Integer pipelineId;
    private Integer projectId;
    private String updatedAt;
    private Commit commit;
}
