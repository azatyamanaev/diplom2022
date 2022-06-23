package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.classifier.models.template.Config;

/**
 * 30.05.2022
 *
 * @author Azat Yamanaev
 */
public interface ConfigRepository extends JpaRepository<Config, Long> {
}
