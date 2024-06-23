//package com.riri.eventhop.events.repository;
//
//import com.riri.eventhop.events.entity.Promotion;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface PromotionRepository extends JpaRepository<Promotion, Long> {
//    List<Promotion> findByEventId(Long eventId);
//    Promotion findByCode(String code);
//}