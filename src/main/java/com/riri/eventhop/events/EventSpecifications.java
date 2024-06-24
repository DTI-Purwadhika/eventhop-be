package com.riri.eventhop.events;

import com.riri.eventhop.events.dto.GetAllEventsParams;
import org.springframework.data.jpa.domain.Specification;
import com.riri.eventhop.events.entity.Event;
import com.riri.eventhop.events.enums.Category;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class EventSpecifications {
    public static Specification<Event> getEvents(GetAllEventsParams params) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (params.getFilter() != null && !params.getFilter().isEmpty()) {
                String filterPattern = "%" + params.getFilter().toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), filterPattern);
                Predicate descriptionPredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), filterPattern);
                predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
            }

            if (params.getCategory() != null) {
                predicates.add(criteriaBuilder.equal(root.get("category"), Category.valueOf(params.getCategory())));
            }

            predicates.add(criteriaBuilder.isNull(root.get("deletedAt")));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
