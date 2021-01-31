package org.app.repository;

import org.app.entity.Material;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Integer> {
    Page<Material> findAllByActiveIsTrue(Pageable pageable);

    Page<Material> findAllByActiveIsFalse(Pageable pageable);

    List<Material> findAllByActiveIsTrueOrderById();

    Material findByName(String name);
}
