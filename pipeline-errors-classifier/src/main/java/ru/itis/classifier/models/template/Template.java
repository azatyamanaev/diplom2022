package ru.itis.classifier.models.template;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import ru.itis.classifier.models.AbstractModel;

import javax.persistence.*;

/**
 * 22.06.2022
 *
 * @author Azat Yamanaev
 */
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "templates")
@SequenceGenerator(name = AbstractModel.GENERATOR, sequenceName = "stages_seq", allocationSize = 1)
public class Template extends AbstractModel {


    @ManyToOne
    @JoinColumn(name = "stage_id")
    private Stage stage;
    private String stageName;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private JsonNode template;
}
