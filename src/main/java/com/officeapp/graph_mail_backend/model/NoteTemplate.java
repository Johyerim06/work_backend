// com.officeapp.graph_mail_backend.model.NoteTemplate.java

package com.officeapp.graph_mail_backend.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "note_templates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NoteTemplate {
    @Id
    private String id;
    private String name;
    private String content;
}

