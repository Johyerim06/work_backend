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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.Base64;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @PostMapping(value = "/sendMail", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> sendMail(
            @RequestParam String to,
            @RequestParam(required = false) String cc,
            @RequestParam(required = false) String bcc,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam(required = false) String from,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = new HashMap<>();
        message.put("subject", subject);

        Map<String, String> bodyContent = new HashMap<>();
        bodyContent.put("contentType", "HTML");
        bodyContent.put("content", body);
        message.put("body", bodyContent);

        List<Map<String, Object>> toRecipients = new ArrayList<>();
        Map<String, Object> toRecipient = new HashMap<>();
        toRecipient.put("emailAddress", Map.of("address", to));
        toRecipients.add(toRecipient);
        message.put("toRecipients", toRecipients);

        if (cc != null && !cc.isBlank()) {
            List<Map<String, Object>> ccRecipients = new ArrayList<>();
            Map<String, Object> ccRecipient = new HashMap<>();
            ccRecipient.put("emailAddress", Map.of("address", cc));
            ccRecipients.add(ccRecipient);
            message.put("ccRecipients", ccRecipients);
        }

        if (bcc != null && !bcc.isBlank()) {
            List<Map<String, Object>> bccRecipients = new ArrayList<>();
            Map<String, Object> bccRecipient = new HashMap<>();
            bccRecipient.put("emailAddress", Map.of("address", bcc));
            bccRecipients.add(bccRecipient);
            message.put("bccRecipients", bccRecipients);
        }

        if (files != null && files.length > 0) {
            List<Map<String, Object>> attachments = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    byte[] fileBytes = file.getBytes();
                    String base64File = Base64.getEncoder().encodeToString(fileBytes);

                    Map<String, Object> attachment = new HashMap<>();
                    attachment.put("@odata.type", "#microsoft.graph.fileAttachment");
                    attachment.put("name", file.getOriginalFilename());
                    attachment.put("contentBytes", base64File);

                    attachments.add(attachment);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            message.put("attachments", attachments);
        }

        Map<String, Object> jsonBody = Map.of(
                "message", message,
                "saveToSentItems", true
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(jsonBody, headers);

        // ✅ 보낼 계정이 지정되어 있으면 users/{email}/sendMail, 아니면 me/sendMail
        String sender = (from != null && !from.isBlank()) ? from : "me";
        String endpoint = "https://graph.microsoft.com/v1.0/users/" + sender + "/sendMail";

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> response = restTemplate.postForEntity(endpoint, request, String.class);
            return response;
        } catch (HttpClientErrorException.Forbidden e) {
            return ResponseEntity.status(403).body("권한이 없습니다. sendAs 권한을 확인하세요.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("메일 전송 실패: " + e.getMessage());
        }
    }

    @GetMapping("/login-url")
    public String loginUrl() {
        return "http://localhost:8080/oauth2/authorization/microsoft";
    }
}