package ru.itis.glabplugin.api.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 22.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class User {

    private Integer id;
    private String name;
    private String username;
    private String avatarUrl;
    private String webUrl;
}
