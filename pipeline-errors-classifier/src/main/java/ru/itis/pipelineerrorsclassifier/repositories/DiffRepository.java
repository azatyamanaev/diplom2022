package ru.itis.pipelineerrorsclassifier.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.itis.pipelineerrorsclassifier.models.Diff;

import java.util.Optional;

/**
 * 30.05.2022
 *
 * @author Azat Yamanaev
 */
public interface DiffRepository extends JpaRepository<Diff, Long> {

    @Query(nativeQuery = true, value = "select * from commit_diffs where commit_id = ?1")
    Optional<Diff> findByCID(String pid);

    @Query(nativeQuery = true, value = "delete from commit_diffs where commit_id = ?1")
    @Modifying
    void deleteByCID(String pid);
}
