package com.erp.p03.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir:uploads/productos}")
    private String uploadDir;

    public String guardarImagen(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("No se puede guardar un archivo vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("El archivo debe ser una imagen");
        }

        String nombreOriginal = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int i = nombreOriginal.lastIndexOf('.');
        if (i > 0) {
            extension = nombreOriginal.substring(i);
        }
        String nombreArchivo = UUID.randomUUID().toString() + extension;

        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path destinoArchivo = uploadPath.resolve(nombreArchivo);
        Files.copy(file.getInputStream(), destinoArchivo, StandardCopyOption.REPLACE_EXISTING);

        return nombreArchivo;
    }

    public void eliminarImagen(String nombreArchivo) throws IOException {
        if (nombreArchivo == null || nombreArchivo.isEmpty()) {
            return;
        }

        Path archivoPath = Paths.get(uploadDir).resolve(nombreArchivo);
        Files.deleteIfExists(archivoPath);
    }

    public Path obtenerRutaArchivo(String nombreArchivo) {
        return Paths.get(uploadDir).resolve(nombreArchivo);
    }
}
