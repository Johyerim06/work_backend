package com.officeapp.graph_mail_backend.repository;

import com.officeapp.graph_mail_backend.dto.QuoteRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuoteListRepository extends MongoRepository<QuoteRequest, String> {

    // 연도 기준 + 세금계산서 미발급 + 학교명 포함 필터
    List<QuoteRequest> findByQuoteDateStartingWithAndNoteIsNullAndSchoolNameContaining(
            String yearPrefix, String schoolName
    );

    // 연도 기준 + 세금계산서 미발급
    List<QuoteRequest> findByQuoteDateStartingWithAndNoteIsNull(String yearPrefix);
}
