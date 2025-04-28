package com.officeapp.graph_mail_backend;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

@RestController
@RequiredArgsConstructor
public class SchoolController {

    private final String API_KEY = "b92d84a43a554f6c96d38d405c292264";

    @GetMapping("/api/schools")
    public String searchSchools(@RequestParam("name") String name) {
        try {
            // ❗ 더 이상 name을 인코딩하지 말고 그대로 사용!!
            String url = "https://open.neis.go.kr/hub/schoolInfo"
                    + "?KEY=" + API_KEY
                    + "&Type=json"
                    + "&pIndex=1"
                    + "&pSize=100"
                    + "&SCHUL_NM=" + name; // ✨ 인코딩 제거

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
            return response.getBody();

        } catch (Exception e) {
            throw new RuntimeException("학교 검색 중 오류가 발생했습니다.", e);
        }
    }
}
