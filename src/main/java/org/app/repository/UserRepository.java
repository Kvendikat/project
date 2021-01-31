package org.app.repository;

import org.app.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

    User findByLogin(String login);

    Page<User> findAllByActiveIsTrue(Pageable pageable);

    Page<User> findAllByActiveIsFalse(Pageable pageable);
}
