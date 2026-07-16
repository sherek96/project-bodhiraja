package com.pirivena_project.pirivena.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class ProfilePictureStorageService {
    private static final Set<String> IMAGE_TYPES = Set.of("image/jpeg", "image/png", "image/webp");

    public String save(MultipartFile file, String previousPicture, String directoryName) {
        if (file == null || file.isEmpty()) throw new RuntimeException("Select a profile picture to upload");
        if (file.getSize() > 5 * 1024 * 1024) throw new RuntimeException("Profile picture must not exceed 5 MB");
        if (!IMAGE_TYPES.contains(file.getContentType())) throw new RuntimeException("Only JPG, PNG and WebP profile pictures are allowed");

        String extension = "image/png".equals(file.getContentType()) ? ".png" : "image/webp".equals(file.getContentType()) ? ".webp" : ".jpg";
        String filename = UUID.randomUUID() + extension;
        try {
            Path directory = Paths.get("uploads", directoryName).toAbsolutePath().normalize();
            Files.createDirectories(directory);
            Files.copy(file.getInputStream(), directory.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
            if (previousPicture != null && !previousPicture.isBlank()) {
                Files.deleteIfExists(directory.resolve(Paths.get(previousPicture).getFileName()));
            }
            return "/uploads/" + directoryName + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not save profile picture", e);
        }
    }
}
