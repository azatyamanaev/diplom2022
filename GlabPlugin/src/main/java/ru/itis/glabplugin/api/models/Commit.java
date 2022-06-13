package ru.itis.glabplugin.api.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * 13.06.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
public class Commit extends AbstractModel {

    private String authorName;
    private String cid;
    private String message;
    private String shortId;
    private String title;

}
