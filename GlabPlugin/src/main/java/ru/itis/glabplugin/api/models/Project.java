package ru.itis.glabplugin.api.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class Project extends AbstractModel {

    private Integer id;
    private String name;
    private String url;
    private String description;
    private boolean tracked;

    @Override
    public String toString() {
        return name;
    }

}
