package org.app.repository;

import org.app.entity.DepartmentBoss;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentBossRepository extends JpaRepository<DepartmentBoss, Integer> {
    List<DepartmentBoss> findAllByActiveTrueOrderByName();
}
