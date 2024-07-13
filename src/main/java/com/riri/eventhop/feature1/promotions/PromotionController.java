package com.riri.eventhop.feature1.promotions;

import com.riri.eventhop.feature1.promotions.dto.PromotionRequest;
import com.riri.eventhop.feature1.promotions.dto.PromotionResponse;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.util.CustomPageable;
import com.riri.eventhop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events/{eventId}/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    @PostMapping
    public ResponseEntity<Response<PromotionResponse>> createPromotion(
            @PathVariable Long eventId,
            @Valid @RequestBody PromotionRequest promotionRequest
    ) {
        PromotionResponse createdPromotion = promotionService.createPromotion(promotionRequest, eventId);
        return Response.success("Promotion created successfully", createdPromotion);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Response<PromotionResponse>> getPromotionById(@PathVariable Long id) {
        PromotionResponse promotion = promotionService.getPromotionById(id);
        return Response.success("Promotion retrieved successfully", promotion);
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<Response<PromotionResponse>> getPromotionByCode(@PathVariable String code) {
        PromotionResponse promotion = promotionService.getPromotionByCode(code);
        return Response.success("Promotion retrieved successfully", promotion);
    }
    @GetMapping("/active")
    public ResponseEntity<Response<Page<PromotionResponse>>> getActivePromotionsForEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort
    ) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        Page<PromotionResponse> promotions = promotionService.getActivePromotionsForEvent(eventId, pageable);
        return Response.success("Active promotions retrieved successfully", promotions);
    }
    @GetMapping
    public ResponseEntity<Response<Page<PromotionResponse>>> getAllPromotionsForEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort
    ) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        Page<PromotionResponse> promotions = promotionService.getAllPromotionsForEvent(eventId, pageable);
        return Response.success("Promotions retrieved successfully", promotions);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Response<PromotionResponse>> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionRequest promotionDetails) {
        PromotionResponse updatedPromotion = promotionService.updatePromotion(id, promotionDetails);
        return Response.success("Promotion updated successfully", updatedPromotion);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Response<Void>> deletePromotion(@PathVariable Long id) {
        promotionService.deletePromotion(id);
        return Response.success("Promotion deleted successfully");
    }
}
