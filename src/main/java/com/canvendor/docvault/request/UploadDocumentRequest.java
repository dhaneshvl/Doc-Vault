package com.canvendor.docvault.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadDocumentRequest {

    @NotBlank(message = "Please provide a file name")
    @Size(max = 32, message = "File name must not exceed {max} characters")
    private String fileName;

    @NotBlank(message = "Please provide the uploader's name")
    @Size(max = 32, message = "Uploader's name must not exceed {max} characters")
    private String uploadedBy;
}
