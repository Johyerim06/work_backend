package com.officeapp.graph_mail_backend.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDate;

@Getter
@Setter
public class TaxInvoiceRequest {

    // MongoDB에서 필터링 및 자동완성에 필요한 필드

    @Field("학교명")
    private String schoolName;

    @Field("견적일자\n가입일자")
    private String quoteDate;

    @Field("세금계산서")
    private String taxInvoice;

    @Field("이메일")
    private String email1;

    @Field("학생수(명)")
    private int studentCount;

    @Field("금액")
    private int amount;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate writeDate;

    // 폼 제출용 필드 (수동 입력 시만 사용)
    private String businessNumber;    // 사업자등록번호
    private String businessName;      // 사업자명
    private String ceoName;           // 대표자명
    private String address;           // 주소
    private String email2;            // 이메일2
}
