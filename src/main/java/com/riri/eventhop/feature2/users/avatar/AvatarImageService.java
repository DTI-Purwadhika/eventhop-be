//package com.riri.eventhop.feature2.users.avatar;
//
//import com.riri.eventhop.exception.ApplicationException;
//import com.riri.eventhop.feature1.images.S3Service;
//import com.riri.eventhop.feature2.users.User;
//import com.riri.eventhop.feature2.users.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.io.IOException;
//
//@Service
//@RequiredArgsConstructor
//public class AvatarImageService {
//
//    private final S3Service s3Service;
//    private final UserRepository userRepository;
//
//    public String uploadAvatarImage(Long userId, MultipartFile file) throws IOException {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with id " + userId));
//        String imageUrl = s3Service.uploadFile(file);
//        user.setAvatarUrl(imageUrl);
//        userRepository.save(user);
//        return imageUrl;
//    }
//
//    public void deleteAvatarImage(Long userId) {
//        User user = userRepository.findById(userId)
//                .orElseThrow(() -> new ApplicationException(HttpStatus.NOT_FOUND, "User not found with id " + userId));
//        String imageUrl = user.getAvatarUrl();
//        if (imageUrl != null && !imageUrl.isEmpty()) {
//            String key = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
//            s3Service.deleteFile(key);
//            user.setAvatarUrl(null);
//            userRepository.save(user);
//        }
//    }
//}
