package com.brenda.recetario.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.Data;

@Service
@Data
public class ImageService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile image) {
        try {
            Map<?, ?> res = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            return (String) res.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("No se pudo subir la imagen", e);
        }
    }

    @Transactional
    public void deleteImage(String url) {
        try {
            String publicId = exgtractPublicIdFromUrl(url);
            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo eliminar la imagen de Cloudinary", e);
        }
    }

    private String exgtractPublicIdFromUrl(String url) {
        try {
            // Get the final part from the URL (eg. "miperro_k7b9lm.jpg")
            String[] parts = url.split("/");
            String fileName = parts[parts.length - 1]; // "miperro_k7b9lm.jpg"

            // Remove extension from the file
            int dot = fileName.lastIndexOf(".");
            return (dot != -1) ? fileName.substring(0, dot) : fileName;
        } catch (Exception e) {
            return null;
        }
    }
}
