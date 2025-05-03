package com.officeapp.graph_mail_backend;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.officeapp.graph_mail_backend.dto.MailTemplate;

public interface MailTemplateRepository extends MongoRepository<MailTemplate, String> { }
