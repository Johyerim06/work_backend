package com.officeapp.graph_mail_backend.dto;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class QuoteSchoolSearchResult {
    @JsonIgnore
    private final Map<String, Object> allFields = new HashMap<>();

    @JsonAnySetter
    public void set(String name, Object value) {
        allFields.put(name, value);
    }

    @JsonAnyGetter
    public Map<String, Object> getAllFields() {
        return allFields;
    }

    public String getSchoolName() {
        return (String) allFields.getOrDefault("학교명", "");
    }

    public String getEmail() {
        return (String) allFields.getOrDefault("이메일", "");
    }

    public int getStudentCount() {
        try {
            return Integer.parseInt(allFields.getOrDefault("학생수(명)", "0").toString().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public int getAmount() {
        try {
            return Integer.parseInt(allFields.getOrDefault("금액", "0").toString().replace(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // 견적일자 (String으로 반환)
    public String getQuoteDateRaw() {
        return (String) allFields.getOrDefault("견적일자\n가입일자", "");
    }

    // 견적일자 (LocalDate로 파싱)
    public LocalDate getQuoteDate() {
        return parseDate(getQuoteDateRaw());
    }

    // 세금계산서 발행일자 (String으로 반환)
    public String getTaxInvoiceRaw() {
        return (String) allFields.getOrDefault("세금계산서", "");
    }

    // 세금계산서 발행일자 (LocalDate로 파싱)
    public LocalDate getTaxInvoiceDate() {
        return parseDate(getTaxInvoiceRaw());
    }

    // 세금계산서 발행 여부
    public boolean isIssued() {
        String tax = getTaxInvoiceRaw();
        return tax != null && !tax.trim().isEmpty();
    }

    private LocalDate parseDate(String str) {
        if (str == null || str.isBlank()) return null;
        String cleaned = str.trim()
                .replace(".", "-")
                .replace("/", "-");

        // 다양한 형식 대응
        String[] formats = {"yyyy-MM-dd", "yy-MM-dd", "yyyy-M-d", "yy-M-d"};

        for (String fmt : formats) {
            try {
                return LocalDate.parse(cleaned, DateTimeFormatter.ofPattern(fmt));
            } catch (DateTimeParseException ignored) {}
        }
        return null;
    }
}