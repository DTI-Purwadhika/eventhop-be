//package com.riri.eventhop.Image;
//
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.model.ObjectMetadata;
//import com.amazonaws.services.s3.model.PutObjectRequest;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//import java.util.UUID;
//
//@Service
//public class ImageStorageService {
//
//    private AmazonS3 amazonS3;
//
//    @Value("${s3.bucketName}")
//    private String bucketName;
//
//    public String uploadAvatar(MultipartFile file) throws IOException {
//        return uploadImage(file, "avatars/");
//    }
//
//    public String uploadImage(MultipartFile file) throws IOException {
//        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
//        String key = "event-images/" + fileName;
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());
//
//        amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
//
//        // Return the URL of the uploaded image
//        return amazonS3.getUrl(bucketName, key).toString();
//    }
//
//    private String uploadImage(MultipartFile file, String folder) throws IOException {
//        String fileName = file.getOriginalFilename();
//        String key = folder + fileName;
//
//        ObjectMetadata metadata = new ObjectMetadata();
//        metadata.setContentLength(file.getSize());
//        metadata.setContentType(file.getContentType());
//
//        amazonS3.putObject(new PutObjectRequest(bucketName, key, file.getInputStream(), metadata));
//
//        // Return the URL of the uploaded image
//        return amazonS3.getUrl(bucketName, key).toString();
//    }
//}
