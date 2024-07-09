package com.riri.eventhop.config;

import com.riri.eventhop.feature1.events.entity.EventCategory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.core.convert.converter.Converter;

@Component
public class StringToEventCategoryConverter implements Converter<String, EventCategory> {
    @Override
    public EventCategory convert(String source) {
        try {
            return EventCategory.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid event category: " + source);
        }
    }
}