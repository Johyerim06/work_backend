package com.officeapp.graph_mail_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuoteRequest {
    private String schoolName;
    private String schoolType; // ex: "초등학교", "중학교", "고등학교"
    private String region;
    private String city;
    private String teacher;
    private String phone;
    private String email;
    private int peopleCount;
    private int months;
    private String startDate; // "yyyy-MM-dd"
    private String endDate;
    private int amount;

    @NotNull(message = "계약 총 금액은 반드시 입력되어야 합니다.")
    private Integer totalAmount; // 💡 반드시 null 체크 위해 Integer로 선언

    private String budget;
    private String requester;
    private String distributor;
    private String distributorSales;
    private String salesRep;
    private String budgetName;
    private String quoteDate;
    private String note;
}
