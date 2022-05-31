package ru.itis.glabplugin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * 23.05.2022
 *
 * @author Azat Yamanaev
 */
@Data
@SuperBuilder
@AllArgsConstructor
public class JobDto {

    private String id;
    private String branch;
}
