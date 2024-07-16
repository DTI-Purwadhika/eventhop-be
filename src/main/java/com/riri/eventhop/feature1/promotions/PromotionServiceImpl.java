package com.riri.eventhop.feature1.promotions;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.exception.ResourceNotFoundException;
import com.riri.eventhop.feature1.events.entity.Event;
import com.riri.eventhop.feature1.events.repository.EventRepository;

import com.riri.eventhop.feature1.promotions.dto.PromotionRequest;
import com.riri.eventhop.feature1.promotions.dto.PromotionResponse;
import com.riri.eventhop.util.CustomPageable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PromotionServiceImpl implements PromotionService {
    private final PromotionRepository promotionRepository;
    private final EventRepository eventRepository;

    @Override
    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")
    public PromotionResponse createPromotion(PromotionRequest promotionRequest, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + eventId));

        Promotion promotion = new Promotion(
                promotionRequest.getName(),
                promotionRequest.getCode(),
                promotionRequest.getType(),
                promotionRequest.getAmount(),
                promotionRequest.getQuota(),
                promotionRequest.getExpireDate(),
                promotionRequest.getStartDate()
        );
        promotion.setEvent(event);

        Promotion savedPromotion = promotionRepository.save(promotion);
        return mapToResponse(savedPromotion);
    }

    @Override
    @PreAuthorize("hasRole('ORGANIZER')")
    public Page<PromotionResponse> getAllPromotionsByOrganizer(Long organizerId, CustomPageable pageable) {
        Page<Promotion> promotions = promotionRepository.findAllByOrganizerId(organizerId, pageable.toPageRequest());
        return promotions.map(this::mapToResponse);
    }
    @Override
    public PromotionResponse getPromotionById(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        return mapToResponse(promotion);
    }

    @Override
    public PromotionResponse getPromotionByCode(String code) {
        Promotion promotion = promotionRepository.findByCode(code)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with code: " + code));
        return mapToResponse(promotion);
    }

    @Override
    public Page<PromotionResponse> getAllPromotionsForEvent(Long eventId, CustomPageable pageable) {
        Page<Promotion> promotions = promotionRepository.findByEventId(eventId, pageable.toPageRequest());
        return promotions.map(this::mapToResponse);
    }

    @Override
    public Page<PromotionResponse> getActivePromotionsForEvent(Long eventId, CustomPageable pageable) {
        LocalDateTime now = LocalDateTime.now();
        Page<Promotion> promotions = promotionRepository.findActivePromotionsForEvent(eventId, now, pageable.toPageRequest());
        return promotions.map(this::mapToResponse);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")
    public PromotionResponse updatePromotion(Long id, PromotionRequest promotionRequest) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));

        promotion.setName(promotionRequest.getName());
        promotion.setCode(promotionRequest.getCode());
        promotion.setType(promotionRequest.getType());
        promotion.setAmount(promotionRequest.getAmount());
        promotion.setQuota(promotionRequest.getQuota());
        promotion.setExpireDate(promotionRequest.getExpireDate());
        promotion.setStartDate(promotionRequest.getStartDate());

        Promotion updatedPromotion = promotionRepository.save(promotion);
        return mapToResponse(updatedPromotion);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('ORGANIZER')")

    public void deletePromotion(Long id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + id));
        promotionRepository.delete(promotion);
    }

    public boolean isPromotionActive(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + promotionId));

        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(promotion.getStartDate()) &&
                now.isBefore(promotion.getExpireDate()) &&
                promotion.getUsedCount() < promotion.getQuota();
    }

    @Transactional
    public void incrementUsedCount(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new ResourceNotFoundException("Promotion not found with id: " + promotionId));

        if (promotion.getUsedCount() >= promotion.getQuota()) {
            throw new ApplicationException("Promotion quota exceeded for promotion: " + promotionId);
        }

        promotion.setUsedCount(promotion.getUsedCount() + 1);
        promotionRepository.save(promotion);
    }

//    private String getCurrentUserEmail() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        if (authentication == null || !authentication.isAuthenticated()) {
//            throw new ApplicationException(HttpStatus.UNAUTHORIZED, "User not authenticated");
//        }
//        if (authentication.getPrincipal() instanceof Jwt jwt) {
//            return jwt.getClaimAsString("email");
//        }
//        throw new ApplicationException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected authentication type");
//    }

    private PromotionResponse mapToResponse(Promotion promotion) {
        PromotionResponse dto = new PromotionResponse();
        dto.setId(promotion.getId());
        dto.setName(promotion.getName());
        dto.setCode(promotion.getCode());
        dto.setType(promotion.getType());
        dto.setAmount(promotion.getAmount());
        dto.setQuota(promotion.getQuota());
        dto.setExpireDate(promotion.getExpireDate());
        dto.setEventName(promotion.getEvent().getTitle());
        return dto;
    }
}