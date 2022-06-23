package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.classifier.models.template.Template;

import java.util.List;

/**
 * 22.06.2022
 *
 * @author Azat Yamanaev
 */
public interface TemplateRepository extends JpaRepository<Template, Long> {

    @Query(nativeQuery = true, value = "select 1 + coalesce((select max(id) from templates), 0)")
    Long findMaxId();

    List<Template> findAllByStageName(String stage);
}
