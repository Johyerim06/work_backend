// src/main/java/com/officeapp/graph_mail_backend/dto/DiverseDocumentRequest.java

package com.officeapp.graph_mail_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DiverseDocumentRequest {
    private String schoolName;
    private String totalAmount;
    private String peopleCount;
    private String startDate;
    private String endDate;
}

