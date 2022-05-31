package ru.itis.pipelineerrorsclassifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.pipelineerrorsclassifier.models.Commit;

import java.util.Optional;

/**
 * 28.05.2022
 *
 * @author Azat Yamanaev
 */
public interface CommitRepository extends JpaRepository<Commit, Long> {

    Optional<Commit> findByPid(String pid);
}
