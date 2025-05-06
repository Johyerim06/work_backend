// com.officeapp.graph_mail_backend.repository.NoteTemplateRepository.java

package com.officeapp.graph_mail_backend.repository;

import com.officeapp.graph_mail_backend.model.NoteTemplate;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NoteTemplateRepository extends MongoRepository<NoteTemplate, String> {
}

