package com.riri.eventhop.events.Image;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class ImageStorageService {

    private final AmazonS3 amazonS3;
    private final String bucketName;

    public ImageStorageService(AmazonS3 amazonS3, @Value("${s3.bucketName}") String bucketName) {
        this.amazonS3 = amazonS3;
        this.bucketName = bucketName;
    }

    public String uploadEventImage(MultipartFile file) throws IOException {
        return uploadImage(file, "event-images/");
    }

    private String uploadImage(MultipartFile file, String folder) throws IOException {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        String key = folder + fileName;

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(file.getSize());
        metadata.setContentType(file.getContentType());

        amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));

        return amazonS3.getUrl(bucketName, key).toString();
    }
}