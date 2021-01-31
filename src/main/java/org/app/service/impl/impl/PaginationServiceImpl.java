package org.app.service.impl.impl;

import org.app.service.impl.PaginationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.stream.IntStream;

@Service
public class PaginationServiceImpl implements PaginationService {
    @Override
    public <T> Model addToModelWithPagination(Model model, Page<T> items, Pageable pageable) {
        model.addAttribute("items", items.getContent());
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("pages", items.getTotalPages());
        model.addAttribute("pagesRange", IntStream.range(0, items.getTotalPages()).toArray());
        model.addAttribute("pageValue", pageable.getPageSize());

        return model;
    }
}
