package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.classifier.models.template.Error;

/**
 * 22.06.2022
 *
 * @author Azat Yamanaev
 */
public interface ErrorRepository extends JpaRepository<Error, Long> {

    @Query(nativeQuery = true, value = "select 1 + coalesce((select max(id) from errors), 0)")
    Long findMaxId();
}
