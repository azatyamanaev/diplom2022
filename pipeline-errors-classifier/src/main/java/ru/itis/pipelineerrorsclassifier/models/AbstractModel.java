package ru.itis.pipelineerrorsclassifier.models;

import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.data.jpa.provider.HibernateUtils;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;
import java.util.StringJoiner;

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
@MappedSuperclass
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public abstract class AbstractModel {

    public static final String ID_COLUMN = "id";
    public static final String GENERATOR = "seq_generator";

    @Id
    @Column(name = ID_COLUMN)
    private Long id;

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", getClass().getSimpleName() + "(", ")")
                .add("id=" + id)
                .toString();
    }
}
