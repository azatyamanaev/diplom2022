package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.classifier.models.PipelineJob;

import java.util.List;
import java.util.Optional;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
public interface PipelineJobRepository extends JpaRepository<PipelineJob, Long> {

    List<PipelineJob> findAllByPipeline_Id(Long id);
}
