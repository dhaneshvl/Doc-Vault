package com.canvendor.docvault.controller;

import com.canvendor.docvault.response.DocumentInfo;
import com.canvendor.docvault.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
@CrossOrigin(value = "*")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping
    public ResponseEntity<String> uploadDocument(@RequestParam("file") MultipartFile file) {
        String documentId = documentService.uploadDocument(file);
        return ResponseEntity.ok().body("Document uploaded successfully with ID: " + documentId);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String documentId) {
        Resource resource = documentService.downloadDocument(documentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping
    public ResponseEntity<List<DocumentInfo>> showAllDocuments() {
        List<DocumentInfo> documents = documentService.getAllDocuments();
        return ResponseEntity.ok().body(documents);
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<String> deleteDocument(@PathVariable String documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.ok().body("Document deleted successfully");
    }

    @PutMapping("/{documentId}")
    public ResponseEntity<String> updateDocument(@PathVariable String documentId, @RequestParam("file") MultipartFile file) {
        documentService.updateDocument(documentId, file);
        return ResponseEntity.ok().body("Document updated successfully");
    }
}
