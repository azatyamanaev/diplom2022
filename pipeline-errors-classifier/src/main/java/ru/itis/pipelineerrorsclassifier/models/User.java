package ru.itis.pipelineerrorsclassifier.models;

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

    private String name;
    private String username;
    private String avatarUrl;
    private String webUrl;
}
