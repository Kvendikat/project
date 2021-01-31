package org.app.repository;

import org.app.entity.LaboratoryBoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LaboratoryBossRepository extends JpaRepository<LaboratoryBoss, Integer> {
    List<LaboratoryBoss> findAllByActiveTrueOrderByName();
}
