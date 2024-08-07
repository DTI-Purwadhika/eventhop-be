package com.riri.eventhop.feature1.promotions;

import com.riri.eventhop.exception.ApplicationException;
import com.riri.eventhop.feature1.promotions.dto.PromotionRequest;
import com.riri.eventhop.feature1.promotions.dto.PromotionResponse;
import com.riri.eventhop.response.PageResponse;
import com.riri.eventhop.response.Response;
import com.riri.eventhop.util.CustomPageable;
import com.riri.eventhop.util.PaginationUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/promotions")
public class PromotionController {

    private final PromotionService promotionService;
    @PostMapping("/events/{eventId}")
    public ResponseEntity<Response<PromotionResponse>> createPromotion(
            @PathVariable Long eventId,
            @RequestBody PromotionRequest promotionDTO) {
        try {
            PromotionResponse createdPromotion = promotionService.createPromotion(promotionDTO, eventId);
            return Response.success("Promotion created successfully", createdPromotion);
        } catch (IllegalArgumentException | ApplicationException e) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @GetMapping("/organizer")
    public ResponseEntity<Response<PageResponse<PromotionResponse>>> getAllPromotionsByOrganizer(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort
    ) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        Page<PromotionResponse> promotionsPage = promotionService.getAllPromotionsByOrganizer(pageable);
        return Response.success("Promotions retrieved successfully", PaginationUtil.createPageResponse(promotionsPage));
    }
    @GetMapping("/events/{eventId}")
    public ResponseEntity<Response<PageResponse<PromotionResponse>>> getAllPromotionsForEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort
    ) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        try {
            Page<PromotionResponse> promotionsPage = promotionService.getAllPromotionsForEvent(eventId, pageable);
            return Response.success("Promotions retrieved successfully", PaginationUtil.createPageResponse(promotionsPage));
        } catch (IllegalArgumentException | ApplicationException e) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @GetMapping("/events/{eventId}/{id}")
    public ResponseEntity<Response<PromotionResponse>> getPromotionById(@PathVariable Long id) {
        try {
            PromotionResponse promotion = promotionService.getPromotionById(id);
            return Response.success("Promotion retrieved successfully", promotion);
        } catch (IllegalArgumentException | ApplicationException e) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @PostMapping("/events/{eventId}/{id}/use")
    public ResponseEntity<Response<Void>> usePromotion(@PathVariable Long id) {
        try {
            promotionService.incrementUsedCount(id);
            return Response.success("Promotion used successfully");
        } catch (ApplicationException e) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }
    @GetMapping("/code/{code}")
    public ResponseEntity<Response<PromotionResponse>> getPromotionByCode(@PathVariable String code) {
        PromotionResponse promotion = promotionService.getPromotionByCode(code);
        return Response.success("Promotion retrieved successfully", promotion);
    }
    @GetMapping("/events/{eventId}/active")
    public ResponseEntity<Response<PageResponse<PromotionResponse>>> getActivePromotionsForEvent(
            @PathVariable Long eventId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) String order,
            @RequestParam(required = false) String sort
    ) {
        CustomPageable pageable = PaginationUtil.createPageable(page, limit, order, sort);
        Page<PromotionResponse> promotionsPage = promotionService.getActivePromotionsForEvent(eventId, pageable);

        return Response.success("Active promotions retrieved successfully", PaginationUtil.createPageResponse(promotionsPage));
    }

    @PutMapping("/events/{eventId}/{id}")
    public ResponseEntity<Response<PromotionResponse>> updatePromotion(@PathVariable Long id, @Valid @RequestBody PromotionRequest promotionDetails) {
        try {
            PromotionResponse updatedPromotion = promotionService.updatePromotion(id, promotionDetails);
            return Response.success("Promotion updated successfully", updatedPromotion);
        } catch (ApplicationException e) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

    @DeleteMapping("/events/{eventId}/{id}")
    public ResponseEntity<Response<Void>> deletePromotion(@PathVariable Long id) {
        try {
            promotionService.deletePromotion(id);
            return Response.success("Promotion deleted successfully");
        } catch (ApplicationException e) {
            return Response.failed(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        }
    }

}
