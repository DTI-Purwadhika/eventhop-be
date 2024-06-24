package com.riri.eventhop.events.dto;

import lombok.Data;

@Data
public class GetAllEventsParams {
    private String filter;
    private String category;
    private Integer limit;
    private Integer page;
}
