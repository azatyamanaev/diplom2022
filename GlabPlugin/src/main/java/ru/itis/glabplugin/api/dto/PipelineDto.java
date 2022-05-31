package ru.itis.glabplugin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 22.05.2022
 *
 * @author Azat Yamanaev
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class PipelineDto {

    private String id;
    private String projectId;
    private String branch;
    private String commit;

}
