package com.canvendor.docvault.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.io.Serializable;
import java.time.LocalDateTime;
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@DynamicInsert
@DynamicUpdate
@Builder
public class DocumentLedger implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentLedgerId;
    private String documentId;
    private String documentName;
    private String documentOriginalName;
    private String uploadedBy;
    private LocalDateTime uploadedDate;
    private String bucketName;
    private String documentType;
    private String operation;
    private String performedBy;
    private LocalDateTime performedDate;
}
