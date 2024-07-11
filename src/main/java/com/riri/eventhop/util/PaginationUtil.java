package com.riri.eventhop.util;

import org.springframework.data.domain.Sort;

public class PaginationUtil {
    public static final int DEFAULT_PAGE = 1;
    public static final int DEFAULT_LIMIT = 20;
    public static final int MAX_LIMIT = 100;
    public static final Sort.Direction DEFAULT_ORDER = Sort.Direction.ASC;
    public static final String DEFAULT_SORT = "id";

    public static CustomPageable createPageable(Integer page, Integer limit, String order, String sort) {
        int validPage = (page != null && page > 0) ? page : DEFAULT_PAGE;
        int validLimit = (limit != null && limit > 0) ? Math.min(limit, MAX_LIMIT) : DEFAULT_LIMIT;
        Sort.Direction validOrder = (order != null) ? Sort.Direction.fromString(order) : DEFAULT_ORDER;
        String validSort = (sort != null && !sort.isEmpty()) ? sort : DEFAULT_SORT;

        return new CustomPageable(validPage, validLimit, validOrder, validSort);
    }
}
