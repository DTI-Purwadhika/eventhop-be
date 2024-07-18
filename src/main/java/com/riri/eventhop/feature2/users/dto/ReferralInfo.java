package com.riri.eventhop.feature2.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ReferralInfo {
    private String referralCode;
    private List<ReferredUser> referredUsers;
}