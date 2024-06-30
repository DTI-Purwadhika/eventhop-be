package com.riri.eventhop.events.controller;

import com.riri.eventhop.events.entity.EventCategory;
import com.riri.eventhop.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    @GetMapping
    public ResponseEntity<Response<List<EventCategory>>> getAllCategories() {
        List<EventCategory> categories = Arrays.asList(EventCategory.values());
        return Response.success("Categories retrieved successfully", categories);
    }
}
