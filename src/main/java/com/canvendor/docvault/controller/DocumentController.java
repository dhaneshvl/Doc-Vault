package com.canvendor.docvault.controller;

import com.canvendor.docvault.request.UploadDocumentRequest;
import com.canvendor.docvault.response.DocumentInfo;
import com.canvendor.docvault.service.DocumentService;
import jakarta.validation.Valid;
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

    @PostMapping("/upload")
    public ResponseEntity<String> uploadDocument(@RequestParam("documentFile") MultipartFile documentFile,
                                                 @Valid @ModelAttribute UploadDocumentRequest uploadDocumentRequest) {
        String documentId = documentService.uploadDocument(documentFile, uploadDocumentRequest);
        return ResponseEntity.ok().body("Document uploaded successfully with name: " + documentId);
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
    public ResponseEntity<String> updateDocument(@PathVariable String documentId,@RequestParam("documentFile") MultipartFile documentFile  , @Valid @RequestBody UploadDocumentRequest uploadDocumentRequest) {
        documentService.updateDocument(documentId, documentFile,uploadDocumentRequest);
        return ResponseEntity.ok().body("Document updated successfully");
    }
}
