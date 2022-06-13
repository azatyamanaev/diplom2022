package ru.itis.classifier.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.util.List;

/**
 * 29.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@Entity
@Table(name = "errors")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "errors_seq", allocationSize = 1)
public class Error extends AbstractModel{

    private String stage;
    private String log;
    private String afterCommand;
    private Type type;

    @ManyToOne
    @JoinColumn(name = "config_id")
    private Config config;

    @OneToMany(mappedBy = "error")
    private List<Pipeline> pipelines;

    public enum Type {
        SCRIPT_FAILURE, UNIT_TEST_ERROR, THIRD_PARTY_SERVICE_ERROR
    }

}
