package com.riri.eventhop.util;

import org.springframework.data.domain.Sort;

public class PaginationUtil {
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;
    public static final Sort.Direction DEFAULT_SORT_DIRECTION = Sort.Direction.ASC;
    public static final String DEFAULT_SORT_BY = "id";

    public static CustomPageable createPageable(Integer page, Integer limit, String sortDirection, String sortBy) {
        int validPage = (page != null && page > 0) ? page : DEFAULT_PAGE;
        int validLimit = (limit != null && limit > 0) ? Math.min(limit, MAX_LIMIT) : DEFAULT_LIMIT;
        Sort.Direction validSortDirection = (sortDirection != null) ? Sort.Direction.fromString(sortDirection) : DEFAULT_SORT_DIRECTION;
        String validSortBy = (sortBy != null && !sortBy.isEmpty()) ? sortBy : DEFAULT_SORT_BY;

        return new CustomPageable(validPage, validLimit, validSortDirection, validSortBy);
    }
}
