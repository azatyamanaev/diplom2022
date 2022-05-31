package ru.itis.pipelineerrorsclassifier.models;

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
@Table(name = "commit_diffs")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "diffs_seq", allocationSize = 1)
public class Diff {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = AbstractModel.GENERATOR)
    private Long id;

    @Column(columnDefinition = "text")
    private String diff;
    private String newPath;
    private String oldPath;
    private String aMode;
    private String bMode;
    private Boolean newFile;
    private Boolean renamedFile;
    private Boolean deletedFile;

    @ManyToOne
    @JoinColumn(name = "commit_id", referencedColumnName = "pid")
    private Commit commit;
}
