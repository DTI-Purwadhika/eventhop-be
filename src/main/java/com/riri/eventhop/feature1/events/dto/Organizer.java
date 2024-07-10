package com.riri.eventhop.feature1.events.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class Organizer implements Serializable {
    private Long id;
    private String name;
}
