//package com.riri.eventhop.events.repository;
//
//import com.riri.eventhop.events.entity.Transaction;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface TransactionRepository extends JpaRepository<Transaction, Long> {
//    List<Transaction> findByEventId(Long eventId);
//}