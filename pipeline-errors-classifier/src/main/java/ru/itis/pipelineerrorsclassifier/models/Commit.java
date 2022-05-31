package ru.itis.pipelineerrorsclassifier.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "commits")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "commits_seq", allocationSize = 1)
public class Commit implements Serializable {


    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = AbstractModel.GENERATOR)
    private Long id;

    @Column(unique = true)
    private String pid;
    private String shortId;
    private String title;
    private String authorName;
    private String authorEmail;
    private String createdAt;
    private String committedAt;
    private String message;
    private String webUrl;

}
