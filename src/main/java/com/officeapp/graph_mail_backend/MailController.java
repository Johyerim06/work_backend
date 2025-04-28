package com.officeapp.graph_mail_backend;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile; // 파일 업로드 받기

import java.util.*;
import java.util.Base64; // Base64 인코딩 추가로 필요!

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @PostMapping(value = "/sendMail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String sendMail(
            @RequestParam String to,
            @RequestParam(required = false) String cc,
            @RequestParam(required = false) String bcc,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestPart(required = false) MultipartFile[] files,
            Authentication authentication
    ) {
        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        oauthToken.getAuthorizedClientRegistrationId(),
                        oauthToken.getName()
                );

        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        System.out.println("Access Token: " + accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 메일 본문 만들기
        Map<String, Object> message = new HashMap<>();
        message.put("subject", subject);

        Map<String, String> bodyContent = new HashMap<>();
        bodyContent.put("contentType", "HTML");
        bodyContent.put("content", body);
        message.put("body", bodyContent);

        // 받는 사람
        List<Map<String, Object>> toRecipients = new ArrayList<>();
        Map<String, Object> toRecipient = new HashMap<>();
        toRecipient.put("emailAddress", Map.of("address", to));
        toRecipients.add(toRecipient);
        message.put("toRecipients", toRecipients);

        // 참조 (cc)
        if (cc != null && !cc.isBlank()) {
            List<Map<String, Object>> ccRecipients = new ArrayList<>();
            Map<String, Object> ccRecipient = new HashMap<>();
            ccRecipient.put("emailAddress", Map.of("address", cc));
            ccRecipients.add(ccRecipient);
            message.put("ccRecipients", ccRecipients);
        }

        // 숨은 참조 (bcc)
        if (bcc != null && !bcc.isBlank()) {
            List<Map<String, Object>> bccRecipients = new ArrayList<>();
            Map<String, Object> bccRecipient = new HashMap<>();
            bccRecipient.put("emailAddress", Map.of("address", bcc));
            bccRecipients.add(bccRecipient);
            message.put("bccRecipients", bccRecipients);
        }

        // ✨ 첨부파일 처리 (여기가 추가된 부분)
        if (files != null && files.length > 0) {
            List<Map<String, Object>> attachments = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    byte[] fileBytes = file.getBytes(); // 파일을 byte 배열로 읽기
                    String base64File = Base64.getEncoder().encodeToString(fileBytes); // Base64 인코딩

                    Map<String, Object> attachment = new HashMap<>();
                    attachment.put("@odata.type", "#microsoft.graph.fileAttachment"); // 파일 첨부임을 명시
                    attachment.put("name", file.getOriginalFilename()); // 파일 이름
                    attachment.put("contentBytes", base64File); // 인코딩된 파일 내용

                    attachments.add(attachment); // 리스트에 추가
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            message.put("attachments", attachments); // ✨ attachments를 메일에 추가
        }

        // 최종 JSON
        Map<String, Object> jsonBody = Map.of(
                "message", message,
                "saveToSentItems", true
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonBody, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://graph.microsoft.com/v1.0/me/sendMail",
                request,
                String.class
        );

        return response.getStatusCode().toString();
    }

    @GetMapping("/login-url")
    public String loginUrl() {
        return "http://localhost:8080/oauth2/authorization/microsoft";
    }
}
