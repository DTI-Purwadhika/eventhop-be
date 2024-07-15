package com.riri.eventhop.feature1.tickets;

import com.riri.eventhop.feature1.tickets.Ticket;
import com.riri.eventhop.feature2.users.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

}
