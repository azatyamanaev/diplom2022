package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.classifier.models.Project;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
public interface ProjectRepository extends JpaRepository<Project, Long> {
}
