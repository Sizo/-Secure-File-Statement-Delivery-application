package com.capitec.statement.api.service;

import com.capitec.statement.api.config.AwsConfig;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3StorageService {

    private final S3Presigner s3Presigner;
    private final AwsConfig awsConfig;

    public S3StorageService(S3Presigner s3Presigner, AwsConfig awsConfig) {
        this.s3Presigner = s3Presigner;
        this.awsConfig = awsConfig;
    }

    public String generatePresignedUrl(String s3Key) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(awsConfig.getS3().getBucket())
                .key(s3Key)
                .build();

        GetObjectPresignRequest getObjectPresignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(awsConfig.getS3().getPresignDuration())
                .getObjectRequest(getObjectRequest)
                .build();

        PresignedGetObjectRequest presignedGetObjectRequest =
                s3Presigner.presignGetObject(getObjectPresignRequest);

        return presignedGetObjectRequest.url().toString();
    }
}
