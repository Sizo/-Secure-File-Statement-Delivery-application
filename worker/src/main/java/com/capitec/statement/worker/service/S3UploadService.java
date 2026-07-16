package com.capitec.statement.worker.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.ServerSideEncryption;

import java.util.UUID;

@Service
public class S3UploadService {

    private static final Logger log = LoggerFactory.getLogger(S3UploadService.class);

    private final S3Client s3Client;
    private final String bucketName;

    public S3UploadService(S3Client s3Client, @Value("${app.aws.s3.bucket}") String bucketName) {
        this.s3Client = s3Client;
        this.bucketName = bucketName;
    }

    public PutObjectResponse uploadStatement(String s3Key, byte[] pdfContent) {
        log.debug("Uploading document to S3 bucket {} with key {}", bucketName, s3Key);

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(s3Key)
                .contentType("application/pdf")
                .serverSideEncryption(ServerSideEncryption.AWS_KMS)
                .build();

        return s3Client.putObject(putObjectRequest, RequestBody.fromBytes(pdfContent));
    }

    public String generateS3Key(String customerId, String statementPeriod, UUID statementId) {
        if (statementPeriod == null || statementPeriod.length() < 7) {
            throw new IllegalArgumentException("Invalid statement period format. Expected YYYY-MM");
        }
        
        String year = statementPeriod.substring(0, 4);
        String month = statementPeriod.substring(5, 7);
        
        return String.format("statements/%s/%s/%s/%s.pdf", customerId, year, month, statementId.toString());
    }
}