package org.app.repository;

import org.app.entity.Type;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TypeRepository extends JpaRepository<Type, Integer> {
    Page<Type> findAllByActiveIsTrue(Pageable pageable);

    Page<Type> findAllByActiveIsFalse(Pageable pageable);

    List<Type> findAllByActiveIsTrueOrderByName();

    Type findByName(String name);
}
