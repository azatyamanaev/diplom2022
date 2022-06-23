package ru.itis.glabplugin.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 22.06.2022
 *
 * @author Azat Yamanaev
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorDto {

    private String stage;
    private String log;
    private String command;
    private String type;

}
