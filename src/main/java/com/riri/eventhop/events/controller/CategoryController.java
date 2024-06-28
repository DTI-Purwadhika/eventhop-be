package com.riri.eventhop.events.controller;

import com.riri.eventhop.events.entity.EventCategory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @GetMapping
    public List<EventCategory> getAllCategories() {
        return Arrays.asList(EventCategory.values());
    }
}
