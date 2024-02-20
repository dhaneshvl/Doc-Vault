package com.canvendor.docvault.service;

import com.amazonaws.services.s3.model.*;
import com.canvendor.docvault.response.DocumentInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.amazonaws.services.s3.AmazonS3;
import java.io.IOException;
import java.util.*;

@Service
public class DocumentService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    public String uploadDocument(MultipartFile file) {
        String documentId = UUID.randomUUID().toString();
        String fileName = file.getOriginalFilename();
        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());
            Map<String, String> userMetadata = new HashMap<>();
            userMetadata.put("fileName", fileName);
            metadata.setUserMetadata(userMetadata);

            amazonS3.putObject(bucketName, documentId, file.getInputStream(), metadata);
        } catch (IOException e) {
            e.printStackTrace();
            // Handle exception
        }
        return documentId;
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

    public void updateDocument(String documentId, MultipartFile file) {
        deleteDocument(documentId); // Delete existing document
        uploadDocument(file); // Upload new document with the same ID
    }
}