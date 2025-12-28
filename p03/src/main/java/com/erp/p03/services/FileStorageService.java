package com.erp.p03.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${gcp.storage.bucket-name}")
    private String bucketName;

    @Value("${gcp.storage.credentials-path}")
    private String credentialsPath;

    private Storage storage;

    @PostConstruct
    public void init() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        this.storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
    }

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

        BlobId blobId = BlobId.of(bucketName, nombreArchivo);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType(contentType).build();

        // Upload file to GCS
        storage.create(blobInfo, file.getBytes());

        // Return the public URL
        // Note: The bucket object must be publicly readable or we need to sign the URL.
        // For simplicity in this project, we assume the bucket or objects are made
        // public or we return the authenticated media link.
        // A common pattern is: https://storage.googleapis.com/<BUCKET_NAME>/<FILE_NAME>

        return "https://storage.googleapis.com/" + bucketName + "/" + nombreArchivo;
    }

    public void eliminarImagen(String urlImagen) {
        if (urlImagen == null || urlImagen.isEmpty()) {
            return;
        }

        // Extract filename from URL
        // URL format: https://storage.googleapis.com/<BUCKET_NAME>/<FILE_NAME>
        try {
            String nombreArchivo = urlImagen.substring(urlImagen.lastIndexOf("/") + 1);
            BlobId blobId = BlobId.of(bucketName, nombreArchivo);
            storage.delete(blobId);
        } catch (Exception e) {
            System.err.println("Error eliminando imagen de GCS: " + e.getMessage());
        }
    }
}
