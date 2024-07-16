package com.tannv.jobhunter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;


@Service
public class FileService {
    @Value("${hoidanit.upload-file.base-uri}")
    private String baseUri;
    public void createDirectory(String folder) throws URISyntaxException {
        URI uri = new URI(folder);
        Path path = Paths.get(uri);
        File tmpDir = new File(path.toString());
        if(!tmpDir.isDirectory()) {
            try {
                Files.createDirectory(tmpDir.toPath());
                System.out.println(">>> Create new directory successfully, path = " + folder);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println(">>> Skip making directory, already existed");
        }
    }

    public String store(MultipartFile file, String folder) throws URISyntaxException, IOException {
        UUID uuid = UUID.randomUUID();
        String originalName = file.getOriginalFilename();
        String extType =  originalName.substring(originalName.lastIndexOf(".") + 1);
        String finalName = uuid.toString() + "." + extType;
        URI uri = new URI(baseUri + folder + "/" + finalName);
        Path path = Paths.get(uri);
        try(InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }
}
