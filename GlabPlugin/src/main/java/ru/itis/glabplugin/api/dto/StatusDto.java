package ru.itis.glabplugin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 22.05.2022
 *
 * @author Azat Yamanaev
 */
@Data
@AllArgsConstructor
public class StatusDto {

    private String status;
    private Double duration;
    private String updatedAt;
}
