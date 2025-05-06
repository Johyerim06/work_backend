package com.officeapp.graph_mail_backend.dto;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "file_metadata")
public class FileMetadata {
    @Id
    private String id;                  // MongoDB에서 문서의 고유 ID
    private String fileName;           // 실제 저장된 파일 이름
    private String path;               // 저장 경로
    private String extension;          // 확장자
    private long size;                 // 바이트 단위 크기
    private LocalDateTime uploadTime; // 업로드 시각
    private String customName;         // 사용자가 지정할 별칭
    private String validUntil;         // 유효기간 (문자열로 입력받기 쉽게)
}
