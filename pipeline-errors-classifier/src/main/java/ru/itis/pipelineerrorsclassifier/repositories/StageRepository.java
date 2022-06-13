package ru.itis.pipelineerrorsclassifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.pipelineerrorsclassifier.models.Stage;

/**
 * 31.05.2022
 *
 * @author Azat Yamanaev
 */
public interface StageRepository extends JpaRepository<Stage, Long> {

    boolean existsByCommandsAndName(String commands, String name);
}
