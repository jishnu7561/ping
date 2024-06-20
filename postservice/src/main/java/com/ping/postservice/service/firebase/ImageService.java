package com.ping.postservice.service.firebase;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.UUID;

@Service
public class ImageService {


    private String uploadFile(File file, String fileName) throws IOException {
        BlobId blobId = BlobId.of("ping-a67a1.appspot.com", fileName); // Updated to correct bucket name format
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("media").build();

        InputStream inputStream = ImageService.class.getClassLoader().getResourceAsStream("ping-ServiceAccountKey.json");

        if (inputStream == null) {
            throw new FileNotFoundException("Firebase private key file not found in classpath");
        }

        Credentials credentials = GoogleCredentials.fromStream(inputStream);
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();
        storage.create(blobInfo, Files.readAllBytes(file.toPath()));

        String DOWNLOAD_URL = "https://firebasestorage.googleapis.com/v0/b/ping-a67a1.appspot.com/o/%s?alt=media";
        return String.format(DOWNLOAD_URL, URLEncoder.encode(fileName, StandardCharsets.UTF_8));
    }


    private File convertToFile(MultipartFile multipartFile, String fileName) throws IOException {
        File tempFile = new File(fileName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(multipartFile.getBytes());
            fos.close();
        }
        return tempFile;
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf("."));
    }


    public String upload(MultipartFile multipartFile) {
        try {
            String fileName = multipartFile.getOriginalFilename();
            fileName = UUID.randomUUID().toString().concat(this.getExtension(fileName));

            File file = this.convertToFile(multipartFile, fileName);
            String URL = this.uploadFile(file, fileName);
            file.delete();
            System.out.println("Image is uploaded");
            return URL;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("Firebase private key file not found: " + e.getMessage());
            return "Image couldn't upload, Firebase private key file not found";
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Image couldn't upload, Something went wrong: " + e.getMessage());
            return "Image couldn't upload, Something went wrong";
        }
    }


//    public String getImageNameByUserId(Integer userId) {
//        // Assuming you have a method in your repository or DAO to find the user by ID
//        User user = userRepository.findById(userId).orElse(null);
//        if (user != null) {
//            // Assuming the user entity has a field for the image name
//            return user.getFilePath();
//        } else {
//            return null; // Or throw an exception or handle the case as needed
//        }
//    }
}

