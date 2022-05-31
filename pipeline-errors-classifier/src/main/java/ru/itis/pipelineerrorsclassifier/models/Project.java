package ru.itis.pipelineerrorsclassifier.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import java.util.List;

/**
 * 19.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "projects")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "projects_seq", allocationSize = 1)
public class Project extends AbstractModel {

    private String name;
    private String defaultBranch;
    private String webUrl;
    private String description;
    private String createdAt;
    private String lastActivityAt;

    @OneToMany(mappedBy = "project")
    private List<Pipeline> pipelines;

}
