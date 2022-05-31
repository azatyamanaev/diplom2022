package ru.itis.pipelineerrorsclassifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.pipelineerrorsclassifier.models.Config;

/**
 * 30.05.2022
 *
 * @author Azat Yamanaev
 */
public interface ConfigRepository extends JpaRepository<Config, Long> {

    boolean existsByStagesListAndCommands(String stagesList, String commands);
}
