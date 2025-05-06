package com.officeapp.graph_mail_backend;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/template")
public class OneTeacherInsertController {

    @PostMapping("/user-insert")
    public String saveUserInsertTemplate(@RequestBody UserTemplateRequest request) throws IOException {
        // 날짜 yymmdd
        String today = new SimpleDateFormat("yyMMdd").format(new Date());
        String baseFileName = "oneteacher_user-insert_" + today + ".csv";

        String email = request.getEmail().toLowerCase();
        String typeValue = "선생님".equals(request.getType()) ? "TEACHER" : "CHEIF";

        // ✅ 헤더와 데이터 한 줄 구성
        String[] headers = { "name", "email", "password", "mobile", "ucode", "grade", "userType", "deptName" };
        String[] data = {
                request.getTeacher(),
                email,
                "1234",
                request.getPhone(),
                email,
                "0",
                typeValue,
                "0"
        };

        // 저장 경로
        List<String> folderPaths = List.of(
                "C:/Users/koro2/OneDrive/다운로드/",
                "C:/Users/koro2/OneDrive/11. 개발/work_mataedu/user-insert_file/"
        );

        for (String folderPath : folderPaths) {
            File folder = new File(folderPath);
            if (!folder.exists()) folder.mkdirs();

            File file = new File(folderPath + baseFileName);
            int count = 1;
            while (file.exists()) {
                String fileNameWithCount = "oneteacher_user-insert_" + today + "(" + count + ").csv";
                file = new File(folderPath + fileNameWithCount);
                count++;
            }

            try (Writer writer = new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8);
                 BufferedWriter bw = new BufferedWriter(writer)) {
                bw.write('\uFEFF'); // BOM
                bw.write(String.join(",", headers));
                bw.newLine();
                bw.write(String.join(",", data));
                bw.newLine();
            }
        }

        return "CSV 양식 저장 완료!";
    }

    @Data
    public static class UserTemplateRequest {
        @NotBlank
        private String teacher;

        @NotBlank
        @Email
        private String email;

        @NotBlank
        private String phone;

        @NotBlank
        private String type;
    }
}
