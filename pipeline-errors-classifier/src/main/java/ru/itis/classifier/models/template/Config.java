package ru.itis.classifier.models.template;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import ru.itis.classifier.models.AbstractModel;
import ru.itis.classifier.models.Pipeline;

import javax.persistence.*;
import java.util.List;

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
@Table(name = "configs")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "configs_seq", allocationSize = 1)
public class Config extends AbstractModel {

    @Column(columnDefinition = "text")
    private String text;
    private String stagesList;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode variables;

    @OneToMany(mappedBy = "config")
    private List<Stage> stages;

    @OneToMany(mappedBy = "config")
    private List<Pipeline> pipelines;

}
