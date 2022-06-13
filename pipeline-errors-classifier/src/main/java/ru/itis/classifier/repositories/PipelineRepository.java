package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.itis.classifier.models.Pipeline;

import java.util.List;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {

    List<Pipeline> findAllByIdLessThan(Long id);

    @Query(nativeQuery = true, value = "select max(created_at) from pipelines where project_id = ?1")
    String lastCreated(Long projectId);
}
