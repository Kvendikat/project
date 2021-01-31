package org.app.repository;

import org.app.entity.WhatFor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WhatForRepository extends JpaRepository<WhatFor, Integer> {

    WhatFor findByTypeAndActiveTrue(String type);

    Page<WhatFor> findAllByActiveTrue(Pageable pageable);

    Page<WhatFor> findAllByActiveFalse(Pageable pageable);

    WhatFor findByType(String type);
}
