package ru.itis.classifier.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.experimental.SuperBuilder;
import ru.itis.classifier.models.template.Config;
import ru.itis.classifier.models.template.Error;

import javax.persistence.*;
import java.util.List;

/**
 * 21.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pipelines")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "pipelines_seq", allocationSize = 1)
public class Pipeline extends AbstractModel {


    private Integer iid;
    private String status;
    private String source;

    @JsonProperty("ref")
    private String branchName;

    @JsonProperty("sha")
    private String commit;
    private String createdAt;
    private String updatedAt;
    private String webUrl;
    private Double duration;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;

    @OneToMany(mappedBy = "pipeline")
    private List<PipelineJob> jobs;

    @ManyToOne
    @JoinColumn(name = "config_id")
    private Config config;

    @ManyToOne
    @JoinColumn(name = "error_id")
    private Error error;
}
