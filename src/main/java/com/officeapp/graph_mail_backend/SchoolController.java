package com.officeapp.graph_mail_backend;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin; // 이거 import 추가
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // ✨ 모든 Origin에서 요청 허용 (개발용 설정)
public class SchoolController {

    private final String API_KEY = "b92d84a43a554f6c96d38d405c292264"; // ✨ NEIS API 키


    @GetMapping("/api/schools")
    public String searchSchools(@RequestParam("name") String name) {
        try {
            // 외부 API 요청 URL
            String url = "https://open.neis.go.kr/hub/schoolInfo"
                    + "?KEY=" + API_KEY
                    + "&Type=json"
                    + "&pIndex=1"
                    + "&pSize=100"
                    + "&SCHUL_NM=" + name;

            // RestTemplate에 타임아웃 설정
            SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
            factory.setConnectTimeout(5000); // 5초 안에 연결 안되면 실패
            factory.setReadTimeout(5000);    // 5초 안에 응답 안 오면 실패
            RestTemplate restTemplate = new RestTemplate(factory);

            // 외부 서버로 요청 보내기
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            return response.getBody(); // 그대로 클라이언트(React)로 전달
        } catch (Exception e) {
            throw new RuntimeException("학교 검색 중 오류가 발생했습니다.", e);
        }
    }
}
