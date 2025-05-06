package com.officeapp.graph_mail_backend;

import com.officeapp.graph_mail_backend.dto.DiverseDocumentRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api")
public class SoftwareLicenseController {

    // ✅ 템플릿 정상 여부 확인용 임시 API
    @GetMapping("/test-template")
    public ResponseEntity<?> testTemplate() {
        try {
            ClassPathResource template = new ClassPathResource("wordtemplates/template_소프트웨어사용권증서.docx");
            XWPFDocument doc = new XWPFDocument(template.getInputStream());
            return ResponseEntity.ok("✅ 템플릿 정상 로딩됨!");
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body("❌ 템플릿 로딩 실패: " + e.getMessage());
        }
    }

    @PostMapping("/generate-license")
    public ResponseEntity<?> generateLicense(@RequestBody DiverseDocumentRequest request, HttpServletResponse response) {
        LocalOfficeManager officeManager = null;
        File tempDocx = null;

        try {
            String todayStr = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String fileName = request.getSchoolName() + "_소프트웨어_사용증서_" + todayStr;

            // 1. 템플릿 로드
            ClassPathResource template = new ClassPathResource("wordtemplates/template_소프트웨어사용권증서.docx");
            XWPFDocument doc = new XWPFDocument(template.getInputStream());

            // 2. 텍스트 치환 (정석적인 방식)
            for (XWPFParagraph paragraph : doc.getParagraphs()) {
                for (XWPFRun run : paragraph.getRuns()) {
                    String text = run.getText(0);
                    if (text != null) {
                        text = text.replace("<<학교명>>", request.getSchoolName());
                        text = text.replace("<<금액>>", String.valueOf(request.getTotalAmount()));
                        text = text.replace("<<인원>>", String.valueOf(request.getPeopleCount()));
                        text = text.replace("<<시작일>>", request.getStartDate());
                        text = text.replace("<<종료일>>", request.getEndDate());
                        run.setText(text, 0); // 기존 텍스트를 교체
                    }
                }
            }

            // 3. 저장 경로
            String[] paths = {
                    "C:/Users/koro2/OneDrive/바탕 화면/새 폴더/소프트웨어 사용권증서",
                    "C:/Users/koro2/마타에듀 주식회사/MATA EDU - 문서/X. 운영위원회/X.01. 법인관련서류/X.01.33. 학습프로그램 소프트웨어 사용권 증서/2025"
            };

            // 4. LibreOffice 변환기 실행
            officeManager = LocalOfficeManager.install();
            officeManager.start();

            tempDocx = File.createTempFile(fileName, ".docx");
            try (FileOutputStream out = new FileOutputStream(tempDocx)) {
                doc.write(out); // 수정된 doc 저장
            }

            for (String path : paths) {
                File dir = new File(path);
                dir.mkdirs();

                File docxFile = new File(path + "/" + fileName + ".docx");
                Files.copy(tempDocx.toPath(), docxFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                File pdfFile = new File(path + "/" + fileName + ".pdf");
                JodConverter.convert(docxFile).to(pdfFile).execute();
            }

            // 5. 브라우저로 다운로드
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedFileName + ".docx\"");
            Files.copy(tempDocx.toPath(), response.getOutputStream());
            response.flushBuffer();

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("문서 생성 중 오류 발생: " + e.getMessage());
        } finally {
            try {
                if (officeManager != null && officeManager.isRunning()) {
                    officeManager.stop();
                }
                if (tempDocx != null && tempDocx.exists()) {
                    tempDocx.deleteOnExit();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}
