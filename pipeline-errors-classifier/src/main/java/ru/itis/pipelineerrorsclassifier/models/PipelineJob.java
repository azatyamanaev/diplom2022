package ru.itis.pipelineerrorsclassifier.models;

import lombok.*;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.nio.channels.Pipe;

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
@Table(name = "pipeline_jobs")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "pipeline_jobs_seq", allocationSize = 1)
public class PipelineJob extends AbstractModel{

    private String name;
    private String stage;
    private String status;
    private Double duration;
    private String webUrl;
    private String logPath;

    @ManyToOne
    @JoinColumn(name = "pipeline_id")
    private Pipeline pipeline;
    private Long projectId;
    private String updatedAt;

}
