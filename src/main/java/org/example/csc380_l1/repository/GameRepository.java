package org.example.csc380_l1.repository;


import org.example.csc380_l1.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Page<Game> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE " +
            "(:name IS NULL OR LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:publisher IS NULL OR LOWER(g.publisher) LIKE LOWER(CONCAT('%', :publisher, '%'))) AND " +
            "(:system IS NULL OR g.system = :system) AND " +
            "(:yearPublished IS NULL OR g.yearPublished = :yearPublished) AND " +
            "(:condition IS NULL OR g.condition = :condition)")
    Page<Game> searchGames(
            @Param("name") String name,
            @Param("publisher") String publisher,
            @Param("system") String system,
            @Param("yearPublished") Integer yearPublished,
            @Param("condition") GameCondition condition,
            Pageable pageable
    );

}
