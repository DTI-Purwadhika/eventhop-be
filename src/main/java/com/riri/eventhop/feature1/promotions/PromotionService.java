package com.riri.eventhop.feature1.promotions;

import com.riri.eventhop.feature1.promotions.dto.PromotionRequest;
import com.riri.eventhop.feature1.promotions.dto.PromotionResponse;
import com.riri.eventhop.util.CustomPageable;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface PromotionService {
    PromotionResponse createPromotion(PromotionRequest promotionRequest, Long eventId);
    PromotionResponse getPromotionById(Long id);
    PromotionResponse getPromotionByCode(String code);
    Page<PromotionResponse> getAllPromotionsForEvent(Long eventId, CustomPageable pageable);
    Page<PromotionResponse> getActivePromotionsForEvent(Long eventId, CustomPageable pageable);
    Page<PromotionResponse> getAllPromotionsByOrganizer(Long organizerId, CustomPageable pageable);
    PromotionResponse updatePromotion(Long id, PromotionRequest promotionRequest);
    void deletePromotion(Long id);
    boolean isPromotionActive(Long promotionId);
    void incrementUsedCount(Long promotionId);
}