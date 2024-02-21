package com.canvendor.docvault.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.canvendor.docvault.model.Document;
import com.canvendor.docvault.model.DocumentLedger;
import com.canvendor.docvault.repository.DocumentLedgerRepository;
import com.canvendor.docvault.repository.DocumentRepository;
import com.canvendor.docvault.request.UploadDocumentRequest;
import com.canvendor.docvault.response.DocumentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class DocumentService {
    @Autowired
    private AmazonS3 amazonS3;
    @Autowired
    private DocumentRepository documentRepository;
    @Autowired
    private DocumentLedgerRepository documentLedgerRepository;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String uploadDocument(MultipartFile documentFile, UploadDocumentRequest uploadDocumentRequest) {
        String documentId = UUID.randomUUID().toString();
        try {

            String fileExtension = documentFile.getContentType().substring(documentFile.getContentType().lastIndexOf('/') + 1);

            // Upload document to Amazon S3
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(documentFile.getSize());
            metadata.setContentType(documentFile.getContentType());
            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("fileName", uploadDocumentRequest.getFileName());
            metadata.setUserMetadata(userMetadata);

            amazonS3.putObject(bucketName, documentId, documentFile.getInputStream(), metadata);


            // Create Document entry
            Document document = createDocumentEntity(documentId, uploadDocumentRequest.getFileName(), documentFile.getOriginalFilename(), uploadDocumentRequest.getUploadedBy(), fileExtension);
            documentRepository.save(document);

            // Create DocumentLedger entry
            DocumentLedger ledgerEntry = createDocumentLedgerEntity(documentId, uploadDocumentRequest.getFileName(), documentFile.getOriginalFilename(), uploadDocumentRequest.getUploadedBy(), fileExtension);
            documentLedgerRepository.save(ledgerEntry);

            fileExtension = null;
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
        return uploadDocumentRequest.getFileName();
    }

    public Resource downloadDocument(String documentId) {
        S3Object object = amazonS3.getObject(bucketName, documentId);
        S3ObjectInputStream stream = object.getObjectContent();
        return new InputStreamResource(stream);
    }

    public List<DocumentInfo> getAllDocuments() {
        List<DocumentInfo> documents = new ArrayList<>();
        ObjectListing objects = amazonS3.listObjects(bucketName);
        for (S3ObjectSummary objectSummary : objects.getObjectSummaries()) {
            String documentId = objectSummary.getKey();

            // Retrieve metadata
            ObjectMetadata metadata = amazonS3.getObjectMetadata(bucketName, documentId);
            Map<String, String> userMetadata = metadata.getUserMetadata();
            String fileName = userMetadata.get("fileName");

            DocumentInfo documentInfo = new DocumentInfo(documentId, fileName);

            documents.add(documentInfo);
        }
        return documents;
    }

    public void deleteDocument(String documentId) {
        amazonS3.deleteObject(bucketName, documentId);
    }

    public void updateDocument(String documentId,MultipartFile documentFile, UploadDocumentRequest uploadDocumentRequest) {
        deleteDocument(documentId); // Delete existing document
        uploadDocument(documentFile,uploadDocumentRequest); // Upload new document with the same ID
    }

    private Document createDocumentEntity(String documentId, String fileName, String originalFileName, String uploadedBy, String contentType) {
        return Document.builder()
                .documentId(documentId)
                .documentName(fileName)
                .documentOriginalName(originalFileName)
                .uploadedBy(uploadedBy)
                .uploadedDate(LocalDateTime.now())
                .bucketName(bucketName)
                .documentType(contentType)
                .build();
    }

    private DocumentLedger createDocumentLedgerEntity(String documentId, String fileName, String originalFileName, String uploadedBy, String contentType) {
        return DocumentLedger.builder()
                .documentId(documentId)
                .documentName(fileName)
                .documentOriginalName(originalFileName)
                .uploadedBy(uploadedBy)
                .uploadedDate(LocalDateTime.now())
                .bucketName(bucketName)
                .documentType(contentType)
                .operation("Upload")
                .performedBy(uploadedBy)
                .performedDate(LocalDateTime.now())
                .build();
    }
}