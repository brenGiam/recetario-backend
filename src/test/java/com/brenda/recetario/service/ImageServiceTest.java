package com.brenda.recetario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Uploader;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private Cloudinary cloudinary;

    @Mock
    private Uploader uploader;

    @InjectMocks
    private ImageService imageService;

    @Test
    void givenValidImage_whenUpload_thenReturnUrl() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenReturn("content".getBytes());

        Map<String, String> result = Map.of("secure_url", "http://img.com/test.png");
        when(cloudinary.uploader()).thenReturn(uploader);
        when(uploader.upload(any(), any())).thenReturn(result);

        String url = imageService.uploadImage(file);
        assertEquals("http://img.com/test.png", url);

        verify(uploader).upload(any(), any());
    }

    @Test
    void givenUploadFails_whenUpload_thenThrowException() throws Exception {
        MultipartFile file = mock(MultipartFile.class);
        when(file.getBytes()).thenThrow(new IOException("Fail"));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> imageService.uploadImage(file));
        assertThat(exception.getMessage()).isEqualTo("No se pudo subir la imagen a Cloudinary");
    }

    @Test
    void givenValidUrl_whenDelete_thenCallDestroy() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);

        imageService.deleteImage("http://res.cloudinary.com/test/image/upload/miperro_k7b9lm.jpg");

        verify(uploader).destroy(eq("miperro_k7b9lm"), any());
    }

    @Test
    void givenDestroyFails_whenDelete_thenThrowException() throws Exception {
        when(cloudinary.uploader()).thenReturn(uploader);
        doThrow(new IOException("Fail")).when(uploader).destroy(any(), any());

        assertThrows(RuntimeException.class,
                () -> imageService.deleteImage("http://res.cloudinary.com/test/image/upload/miperro_k7b9lm.jpg"));
    }
}
