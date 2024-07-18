package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.feature1.events.service.EventService;
import com.riri.eventhop.feature1.tickets.dto.PurchaseHistory;
import com.riri.eventhop.feature1.tickets.dto.TicketPurchaseRequest;
import com.riri.eventhop.feature1.tickets.dto.TicketResponse;
import com.riri.eventhop.feature1.tickets.dto.TransactionHistory;
import com.riri.eventhop.feature1.tickets.entity.Ticket;
import com.riri.eventhop.feature2.users.auth.AuthService;
import com.riri.eventhop.feature2.users.entity.User;
import com.riri.eventhop.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;
    private final AuthService authService;
    private final EventService eventService;
    @PostMapping("/purchase")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response<TicketResponse>> purchaseTicket(
            @RequestBody TicketPurchaseRequest request) {

        User user = authService.findById(authService.getCurrentUserId());
        Ticket ticket = ticketService.purchaseTicket(
                request.getEventId(),
                request.getTierName(),
                user,
                request.getPromotionCode(),
                request.getPointsToUse(),
                request.isUseReferralDiscount()
        );

        TicketResponse ticketResponse = mapTicketToResponse(ticket);
        return Response.success("Ticket purchased successfully", ticketResponse);
    }
    private TicketResponse mapTicketToResponse(Ticket ticket) {
        TicketResponse response = new TicketResponse();
        response.setId(ticket.getId());
        response.setTicketCode(ticket.getTicketCode());
        response.setEventTitle(ticket.getEvent().getTitle());
        response.setEventId(ticket.getEvent().getId());
        response.setTicketTierName(ticket.getTicketTier().getName());
        response.setPrice(ticket.getPrice());
        response.setEventStartTime(ticket.getEvent().getStartTime());
        response.setEventEndTime(ticket.getEvent().getEndTime());
        response.setEventLocation(ticket.getEvent().getLocation());
        response.setPurchaserName(ticket.getUser().getName());
        response.setPurchaseTime(ticket.getCreatedAt());
        response.setUsed(ticket.isUsed());
        return response;
    }


    @GetMapping("/purchase-history")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Response<List<PurchaseHistory>>> getPurchaseHistory() {
        User user = authService.findById(authService.getCurrentUserId());
        List<Ticket> tickets = ticketService.getPurchaseHistory(user);

        List<PurchaseHistory> purchaseHistories = tickets.stream()
                .map(this::mapTicketToPurchaseHistory)
                .collect(Collectors.toList());

        return Response.success("User's purchase history retrieved successfully", purchaseHistories);
    }

    @GetMapping("/transaction-history")
    @PreAuthorize("hasRole('ORGANIZER')")
    public ResponseEntity<Response<List<TransactionHistory>>> getTransactionHistory() {
        User organizer = authService.findById(authService.getCurrentUserId());
        List<Ticket> tickets = ticketService.getTransactionHistoryForOrganizer(organizer);

        List<TransactionHistory> transactionHistories = tickets.stream()
                .map(this::mapTicketToTransactionHistory)
                .collect(Collectors.toList());

        return Response.success("Organizer's transaction history retrieved successfully", transactionHistories);
    }

    private PurchaseHistory mapTicketToPurchaseHistory(Ticket ticket) {
        PurchaseHistory history = new PurchaseHistory();
        history.setTitle(ticket.getEvent().getTitle());
        history.setTicketTier(ticket.getTicketTier().getName());
        history.setPurchaseTime(ticket.getCreatedAt());
        return history;
    }

    private TransactionHistory mapTicketToTransactionHistory(Ticket ticket) {
        TransactionHistory history = new TransactionHistory();
        history.setName(ticket.getUser().getName());
        history.setTitle(ticket.getEvent().getTitle());
        history.setTicketTier(ticket.getTicketTier().getName());
        return history;
    }

}