package com.riri.eventhop.feature1.events.entity;

import lombok.Getter;

@Getter
public enum EventCategory {
    TECHNOLOGY("Technology"),
    HEALTH_AND_WELLNESS("Health & Wellness"),
    BUSINESS("Business"),
    ENTERTAINMENT("Entertainment"),
    EDUCATION("Education"),
    SPORTS("Sports"),
    TRAVEL("Travel"),
    FOOD_AND_DRINK("Food & Drink"),
    FASHION("Fashion"),
    ART_AND_CULTURE("Art & Culture"),
    SCIENCE("Science"),
    POLITICS("Politics"),
    ENVIRONMENT("Environment"),
    AUTOMOTIVE("Automotive"),
    GAMING("Gaming");
    private final String category;

    EventCategory(String category) {
        this.category = category;
    }

}
