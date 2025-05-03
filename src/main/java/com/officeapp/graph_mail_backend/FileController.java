package com.officeapp.graph_mail_backend;

import com.officeapp.graph_mail_backend.dto.FileMetadata;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final String uploadDir = "C:/Users/koro2/OneDrive/11. 개발/work_mataedu/upload_document_file";

    @Autowired
    private MongoTemplate mongoTemplate;

    // 1. 파일 업로드
    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            String extension = "";

            if (fileName != null && fileName.contains(".")) {
                extension = fileName.substring(fileName.lastIndexOf('.') + 1).toUpperCase(); // 확장자 대문자 변환
            }

            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            File dest = new File(uploadDir + File.separator + fileName);
            file.transferTo(dest);

            FileMetadata metadata = new FileMetadata();
            metadata.setFileName(fileName);
            metadata.setExtension(extension);
            metadata.setPath(dest.getAbsolutePath());
            metadata.setSize(file.getSize());
            metadata.setUploadTime(LocalDateTime.now());
            metadata.setCustomName("");
            metadata.setValidUntil("");

            mongoTemplate.save(metadata, "file_metadata");

            return ResponseEntity.ok("파일 업로드 성공");
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("업로드 실패: " + e.getMessage());
        }
    }

    // 2. 파일 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<FileMetadata>> listFiles() {
        List<FileMetadata> files = mongoTemplate.findAll(FileMetadata.class, "file_metadata");
        return ResponseEntity.ok(files);
    }

    // 3. 사용자 지정 파일명(customName) 업데이트
    @PatchMapping("/update-name/{id}")
    public ResponseEntity<String> updateCustomName(@PathVariable String id, @RequestParam String customName) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update().set("customName", customName);

        mongoTemplate.updateFirst(query, update, "file_metadata");

        return ResponseEntity.ok("지정 파일명 업데이트 성공");
    }

    // 4. 유효기간(validUntil) 업데이트
    @PatchMapping("/update-valid-until/{id}")
    public ResponseEntity<String> updateValidUntil(@PathVariable String id, @RequestParam String validUntil) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        Update update = new Update().set("validUntil", validUntil);

        mongoTemplate.updateFirst(query, update, "file_metadata");

        return ResponseEntity.ok("유효기간 업데이트 성공");
    }

    // 5. 파일 삭제
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteFile(@PathVariable String id) {
        Query query = new Query(Criteria.where("_id").is(new ObjectId(id)));
        mongoTemplate.remove(query, "file_metadata");

        return ResponseEntity.ok("파일 메타데이터 삭제 완료");
    }
}
