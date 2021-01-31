package org.app.repository;

import org.app.entity.Producer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProducerRepository extends JpaRepository<Producer, Integer> {
    Page<Producer> findAllByActiveIsTrue(Pageable pageable);

    Page<Producer> findAllByActiveIsFalse(Pageable pageable);

    Producer findByName(String name);
}
