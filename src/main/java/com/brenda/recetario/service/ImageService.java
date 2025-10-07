package com.brenda.recetario.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Data
public class ImageService {
    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile image) {
        try {
            Map<?, ?> res = cloudinary.uploader().upload(image.getBytes(), ObjectUtils.emptyMap());
            String url = (String) res.get("secure_url");
            log.info("Imagen subida correctamente a Cloudinary: {}", url);
            return url;
        } catch (IOException e) {
            log.error("Error subiendo imagen a Cloudinary", e);
            throw new RuntimeException("No se pudo subir la imagen", e);
        }
    }

    public void deleteImage(String url) {
        try {
            String publicId = extractPublicIdFromUrl(url);
            if (publicId != null && !publicId.isEmpty()) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
                log.info("Imagen eliminada correctamente de Cloudinary: {}", url);
            } else {
                log.warn("No se pudo extraer publicId de la URL: {}", url);
            }
        } catch (IOException e) {
            log.error("Error eliminado imagen de Cloudinary: {}", e);
            throw new RuntimeException("No se pudo eliminar la imagen de Cloudinary", e);
        }
    }

    private String extractPublicIdFromUrl(String url) {
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
