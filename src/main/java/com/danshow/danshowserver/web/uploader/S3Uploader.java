package com.danshow.danshowserver.web.uploader;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.Upload;
import com.amazonaws.util.IOUtils;
import com.danshow.danshowserver.domain.video.AttachFile;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.io.*;
import java.util.UUID;

@Service
@NoArgsConstructor
@Slf4j
public class S3Uploader {
    private AmazonS3 s3Client;

    @Value("${cloud.aws.credentials.accessKey}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secretKey}")
    private String secretKey;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.region.static}")
    private String region;

    @PostConstruct
    public void setS3Client() {
        AWSCredentials credentials = new BasicAWSCredentials(this.accessKey, this.secretKey);

        s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(credentials))
                .withRegion(this.region)
                .build();
    }

    public String upload(MultipartFile file, String s3SavePath) throws IOException {
        return upload(file,file.getOriginalFilename(),s3SavePath);
    }

    public String upload(MultipartFile file, String customFileName, String s3SavePath) throws IOException {
        s3SavePath = s3SavePath + "/" + customFileName;
        s3Client.putObject(new PutObjectRequest(bucket, s3SavePath, file.getInputStream(), null)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        return s3Client.getUrl(bucket, s3SavePath).toString();
    }

    public String upload(String filePath, String fileName, String s3SavePath) throws IOException {
        File file = new File(filePath);
        s3SavePath = s3SavePath + "/" + fileName;
        s3Client.putObject(new PutObjectRequest(bucket, s3SavePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));
        log.info("s3 uploaded complete");
        return s3Client.getUrl(bucket, s3SavePath).toString();
    }

    public String upload(File file, String s3SavePath) throws IOException {
        s3SavePath = s3SavePath + "/" + file.getName();
        s3Client.putObject(new PutObjectRequest(bucket, s3SavePath, file)
                .withCannedAcl(CannedAccessControlList.PublicRead));

        log.info("s3 uploaded complete");
        return s3Client.getUrl(bucket, s3SavePath).toString();
    }

    public byte[] getObject(AttachFile attachFile) throws IOException {
        S3Object o = s3Client.getObject(new GetObjectRequest(bucket+"/video",attachFile.getFilename()));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);
        return bytes;
    }

    public byte[] getObject(String filePath) throws IOException {
        //S3Object o = s3Client.getObject(new GetObjectRequest(bucket+"/video",));
        S3Object o = s3Client.getObject(new GetObjectRequest(bucket+"/audio",filePath));
        S3ObjectInputStream objectInputStream = o.getObjectContent();
        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        return bytes;
    }




}
