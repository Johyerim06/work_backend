package com.officeapp.graph_mail_backend;

import com.officeapp.graph_mail_backend.dto.QuoteRequest;
import jakarta.validation.Valid;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jodconverter.local.JodConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestController
@RequestMapping("/api/contract")
public class ContractController {

    @PostMapping("/quote")
    public ResponseEntity<String> generateQuote(@RequestBody @Valid QuoteRequest request) throws Exception {
        InputStream templateStream = getClass().getResourceAsStream("/exceltemplates/template_견적서.xlsx");
        Workbook workbook = new XSSFWorkbook(templateStream);
        Sheet sheet = workbook.getSheetAt(0);

        String todayForCell = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));
        String todayForFile = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String quoteDate = (request.getQuoteDate() != null && !request.getQuoteDate().isEmpty())
                ? request.getQuoteDate()
                : todayForCell;

        String region = request.getRegion();
        String city = request.getCity();
        String schoolName = request.getSchoolName();
        String receiver = schoolName != null && schoolName.startsWith(city) ? schoolName : city + " " + schoolName;
        String teacher = request.getTeacher() + " 선생님";

        String period;
        try {
            if (request.getStartDate() != null && request.getEndDate() != null &&
                    !request.getStartDate().isBlank() && !request.getEndDate().isBlank()) {
                LocalDate start = LocalDate.parse(request.getStartDate());
                LocalDate end = LocalDate.parse(request.getEndDate());
                int months = request.getMonths() > 0 ? request.getMonths() :
                        (end.getYear() - start.getYear()) * 12 + (end.getMonthValue() - start.getMonthValue()) + 1;
                period = String.format("%s\n~\n%s\n(%d개월)",
                        start.format(DateTimeFormatter.ofPattern("yy.MM.dd")),
                        end.format(DateTimeFormatter.ofPattern("yy.MM.dd")),
                        months);
            } else {
                period = request.getMonths() + "개월";
            }
        } catch (Exception e) {
            period = request.getMonths() + "개월";
        }

        int amount = request.getAmount();
        int people = request.getPeopleCount();
        int totalAmount = request.getTotalAmount();
        int supplyAmount = (int)Math.floor(totalAmount / 1.1);
        int vat = totalAmount - supplyAmount;

        sheet.getRow(9).getCell(4).setCellValue(quoteDate);               // E10
        sheet.getRow(11).getCell(4).setCellValue(receiver);               // E12
        sheet.getRow(14).getCell(4).setCellValue(teacher);                // E15
        sheet.getRow(26).getCell(3).setCellValue(period);                 // D27
        sheet.getRow(26).getCell(5).setCellValue(people);                 // F27
        sheet.getRow(26).getCell(7).setCellValue(amount);                 // H27
        sheet.getRow(26).getCell(11).setCellValue(totalAmount);          // L27
        sheet.getRow(27).getCell(3).setCellValue(request.getNote());     // D28

        sheet.getRow(26).getCell(13).setCellValue(supplyAmount);         // N27
        sheet.getRow(26).getCell(15).setCellValue(vat);                  // P27
        sheet.getRow(27).getCell(11).setCellValue(totalAmount);          // L28
        sheet.getRow(27).getCell(13).setCellValue(supplyAmount);         // N28
        sheet.getRow(27).getCell(15).setCellValue(vat);                  // P28

        Map<String, String> folderMap = Map.ofEntries(
                Map.entry("충북", "06. 충북"), Map.entry("경북", "15. 경북"),
                Map.entry("대구", "11. 대구"), Map.entry("전남", "09. 전남"),
                Map.entry("서울", "01. 서울"), Map.entry("대전", "07. 대전"),
                Map.entry("충남", "05. 충남"), Map.entry("경기", "02. 경기, 세종"),
                Map.entry("전북", "10. 전북"), Map.entry("제주", "16. 제주"),
                Map.entry("경남", "14. 경남"), Map.entry("인천", "03. 인천"),
                Map.entry("광주", "08. 광주"), Map.entry("강원", "04. 강원도"),
                Map.entry("부산", "12. 부산"), Map.entry("울산", "13. 울산")
        );

        String folderName = folderMap.getOrDefault(region, "기타");
        String fileName = todayForFile + "_견적서_" + schoolName;

        String[] basePaths = {
                "C:/Users/koro2/마타에듀 주식회사/MATA EDU - 문서/X. 운영위원회/X.03. 수발신문서/X.03.02. 공문외_발신서류/X.03.02.12. 발신견적서/2025/마타수학/" + folderName,
                "C:/Users/koro2/Downloads",
                "C:/Users/koro2/OneDrive/다운로드/마타에듀견적서"
        };

        LocalOfficeManager officeManager = LocalOfficeManager.install();
        officeManager.start();

        for (String path : basePaths) {
            File dir = new File(path);
            dir.mkdirs();

            String excelPath = path + "/" + fileName + ".xlsx";
            String pdfPath = path + "/" + fileName + ".pdf";

            try (FileOutputStream out = new FileOutputStream(excelPath)) {
                workbook.write(out);
            }

            JodConverter
                    .convert(new File(excelPath))
                    .to(new File(pdfPath))
                    .execute();
        }

        workbook.close();
        officeManager.stop();

        return ResponseEntity.ok("엑셀과 PDF 저장 완료!");
    }
}
