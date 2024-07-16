package com.tannv.jobhunter.controller;

import com.tannv.jobhunter.domain.response.file.ResUploadFileDTO;
import com.tannv.jobhunter.service.FileService;
import com.tannv.jobhunter.util.anotation.ApiMessage;
import com.tannv.jobhunter.util.error.StorageException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {
    private final FileService fileService;

    @Value("${hoidanit.upload-file.base-uri}")
    private String baseUri;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping
    @ApiMessage("Upload single file")
    public ResponseEntity<ResUploadFileDTO> uploadFile(@RequestParam(name = "file", required = false)MultipartFile file, @RequestParam("folder") String folder) throws URISyntaxException, IOException, StorageException {
        if(file == null || file.isEmpty()) {
            throw new StorageException("File is empty. Please upload a file.");
        }
        String fileName = file.getOriginalFilename();
        List<String> allowedExtension = Arrays.asList("pdf", "jpg", "jpeg", "png", "doc", "docx");
        boolean isValidExt = allowedExtension.stream().anyMatch(item -> item.toLowerCase().endsWith(item));
        if(isValidExt) {
            throw new StorageException("File extension is invalid.");
        }
        this.fileService.createDirectory(baseUri + "/" + folder);
        String uploadedFile = this.fileService.store(file, folder);
        ResUploadFileDTO resUploadFileDTO = new ResUploadFileDTO(uploadedFile, Instant.now());
        return ResponseEntity.ok(resUploadFileDTO);
    }
}
