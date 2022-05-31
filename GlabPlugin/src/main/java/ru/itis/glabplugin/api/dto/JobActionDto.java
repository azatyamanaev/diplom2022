package ru.itis.glabplugin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 26.05.2022
 *
 * @author Azat Yamanaev
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class JobActionDto {

    private String status;
    private boolean retried;
}
