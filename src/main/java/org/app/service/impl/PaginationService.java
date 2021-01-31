package org.app.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

public interface PaginationService {
    <T> Model addToModelWithPagination(Model model, Page<T> items, Pageable pageable);
}
