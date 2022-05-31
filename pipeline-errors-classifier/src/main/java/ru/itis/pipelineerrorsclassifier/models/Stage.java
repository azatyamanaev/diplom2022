package ru.itis.pipelineerrorsclassifier.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;

/**
 * 31.05.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "stages")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "stages_seq", allocationSize = 1)
public class Stage extends AbstractModel {

    private String name;

    @Column(columnDefinition = "text")
    private String commands;

    @ManyToOne
    @JoinColumn(name = "config_id")
    private Config config;

}
