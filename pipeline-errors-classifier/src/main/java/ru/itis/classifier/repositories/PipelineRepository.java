package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
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

    List<Pipeline> findAllByIdGreaterThanAndProject_IdAndStatus(Long id, Long projectId, String status);

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "update pipelines set error_id = ?1 where id = ?2")
    void update(Long eid, Long pipelineId);
}
