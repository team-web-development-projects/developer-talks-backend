package com.dtalks.dtalks.base.component;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.dtalks.dtalks.exception.ErrorCode;
import com.dtalks.dtalks.exception.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Uploader {

    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public static String createFilePath(String fileName, String path) {
        Date date = new Date();
        return path + "/" + date.getTime() + "_" + UUID.randomUUID() + fileName;
    }

    public void deleteFile(String path) {
        amazonS3Client.deleteObject(bucket, path);
    }

    public String fileUpload(MultipartFile multipartFile, String path) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(multipartFile.getContentType());
        objectMetadata.setContentLength(multipartFile.getSize());

        try {
            amazonS3Client.putObject(bucket, path, multipartFile.getInputStream(), objectMetadata);
        }
        catch (IOException e) {
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR, "파일 업로드에 실패하였습니다.");
        }
        return amazonS3Client.getUrl(bucket, path).toString();
    }
}
