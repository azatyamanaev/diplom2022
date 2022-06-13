package ru.itis.classifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.classifier.models.Commit;

import java.util.Optional;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
public interface CommitRepository extends JpaRepository<Commit, Long> {

    Optional<Commit> findByPid(String pid);
}
