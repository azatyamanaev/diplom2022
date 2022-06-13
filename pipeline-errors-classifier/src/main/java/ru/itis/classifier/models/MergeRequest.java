package ru.itis.classifier.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * 30.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "merge_requests")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "merge_request_seq", allocationSize = 1)
public class MergeRequest extends AbstractModel {

    private Integer iid;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private Project project;
    private String title;
    private String description;
    private String state;
    private String targetBranch;
    private String sourceBranch;
    private Boolean draft;
    private String mergeStatus;

    @JsonProperty("sha")
    private String commit;
    private String mergeCommit;
    private String webUrl;
}
