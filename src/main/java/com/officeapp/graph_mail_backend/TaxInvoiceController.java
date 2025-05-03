package com.officeapp.graph_mail_backend;

import com.officeapp.graph_mail_backend.dto.TaxInvoiceRequest;
import com.officeapp.graph_mail_backend.dto.QuoteSchoolSearchResult;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/tax-invoice")
@RequiredArgsConstructor
public class TaxInvoiceController {

    private final MongoTemplate mongoTemplate;

    @PostMapping("/generate")
    public ResponseEntity<String> generateTaxInvoice(@RequestBody @Valid TaxInvoiceRequest request) throws Exception {
        InputStream templateStream = getClass().getResourceAsStream("/exceltemplates/template_tax-invoice-issue-file.xlsx");
        Workbook workbook = new XSSFWorkbook(templateStream);
        Sheet sheet = workbook.getSheetAt(0);

        int rowIndex = 6;
        Row row = sheet.getRow(rowIndex);
        if (row == null) row = sheet.createRow(rowIndex);

        String writeDate = request.getWriteDate().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String writeDayOnly = String.format("%02d", request.getWriteDate().getDayOfMonth());
        int totalAmount = request.getAmount();
        int taxAmount = totalAmount / 11;
        int supplyAmount = totalAmount - taxAmount;

        row.createCell(0).setCellValue("01");
        row.createCell(1).setCellValue(writeDate);
        row.createCell(2).setCellValue("1208802692");
        row.createCell(4).setCellValue("마타에듀 주식회사");
        row.createCell(5).setCellValue("오태형");
        row.createCell(6).setCellValue("서울특별시 강남구 논현로 616, 4층(논현동, 대일빌딩)");
        row.createCell(7).setCellValue("정보통신업");
        row.createCell(8).setCellValue("시스템 소프트웨어 개발 및 공급업");
        row.createCell(9).setCellValue("jihwan.park@mataedu.com");

        row.createCell(10).setCellValue(request.getBusinessNumber());
        row.createCell(12).setCellValue(request.getBusinessName());
        row.createCell(13).setCellValue(request.getCeoName());
        row.createCell(14).setCellValue(request.getAddress());
        row.createCell(17).setCellValue(request.getEmail1());
        row.createCell(18).setCellValue(request.getEmail2());

        row.createCell(19).setCellValue(supplyAmount);
        row.createCell(20).setCellValue(taxAmount);
        row.createCell(22).setCellValue(writeDayOnly);
        row.createCell(23).setCellValue("AI활용 수학학습 프로그램 사용권");
        row.createCell(27).setCellValue(supplyAmount);
        row.createCell(28).setCellValue(taxAmount);
        row.createCell(31).setCellValue("01");

        String fileName = request.getBusinessName() + "_" + request.getWriteDate().format(DateTimeFormatter.ofPattern("yyMMdd")) + ".xlsx";

        Path savePath1 = Path.of("C:/Users/koro2/OneDrive/11. 개발/work_mataedu/hometax/tax_invoice", fileName);
        Path savePath2 = Path.of("C:/Users/koro2/OneDrive/다운로드", fileName);

        try (OutputStream os1 = Files.newOutputStream(savePath1); OutputStream os2 = Files.newOutputStream(savePath2)) {
            workbook.write(os1);
            workbook.write(os2);
        }

        workbook.close();
        return ResponseEntity.ok("세금계산서 발급 완료: " + fileName);
    }

    @GetMapping("/school-list")
    public ResponseEntity<List<Map<String, Object>>> searchSchools(@RequestParam String keyword) {
        Query query = new Query();
        query.addCriteria(Criteria.where("학교명").regex(keyword));
        query.addCriteria(Criteria.where("견적일자\n가입일자").ne(null));

        List<Map> results = mongoTemplate.find(query, Map.class, "quote_list");
        List<Map<String, Object>> converted = new ArrayList<>();

        for (Map doc : results) {
            Map<String, Object> item = new HashMap<>();
            item.put("schoolName", doc.getOrDefault("학교명", ""));
            item.put("email", doc.getOrDefault("이메일", ""));
            item.put("studentCount", doc.getOrDefault("학생수(명)", 0));
            item.put("amount", parseAmount(doc.get("금액")));
            item.put("quoteDate", doc.getOrDefault("견적일자\n가입일자", ""));
            item.put("taxInvoiceDate", doc.getOrDefault("세금계산서", ""));
            item.put("issued", doc.containsKey("세금계산서") && doc.get("세금계산서") != null && !doc.get("세금계산서").toString().isBlank());
            converted.add(item);
        }

        return ResponseEntity.ok(converted);
    }

    private int parseAmount(Object amount) {
        if (amount == null) return 0;
        try {
            return Integer.parseInt(amount.toString().replaceAll(",", ""));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
