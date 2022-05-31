package ru.itis.pipelineerrorsclassifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.pipelineerrorsclassifier.models.PipelineJob;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
public interface PipelineJobRepository extends JpaRepository<PipelineJob, Long> {
}
