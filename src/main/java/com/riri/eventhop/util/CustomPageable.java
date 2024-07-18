package com.riri.eventhop.util;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@Data
@AllArgsConstructor
public class CustomPageable {
    private int page;
    private int limit;
    private Sort.Direction order;
    private String sort;

    public PageRequest toPageRequest() {
        return PageRequest.of(page - 1, limit, Sort.by(order, sort));
    }
}