package com.officeapp.graph_mail_backend.dto;

import org.springframework.data.annotation.Id;

public class MailTemplate {
    @Id
    private String id;
    private String name;
    private String subject;
    private String content;

    public MailTemplate() {}

    public MailTemplate(String name, String subject, String content) {
        this.name = name;
        this.subject = subject;
        this.content = content;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
