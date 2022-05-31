package ru.itis.glabplugin.api.models;

import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
@SuperBuilder
@NoArgsConstructor
public abstract class AbstractModel {

    public Number getId() {
        return null;
    }
}
