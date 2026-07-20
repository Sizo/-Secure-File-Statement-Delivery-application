package com.capitec.statement.api.service;

import com.capitec.statement.api.config.AwsConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.net.URL;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3StorageServiceTest {

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private AwsConfig awsConfig;

    @InjectMocks
    private S3StorageService s3StorageService;

    @Test
    void generatePresignedUrl_ReturnsValidUrl() throws Exception {
        AwsConfig.S3Properties s3Props = new AwsConfig.S3Properties();
        s3Props.setBucket("test-bucket");
        s3Props.setPresignDurationMinutes(15);
        when(awsConfig.getS3()).thenReturn(s3Props);

        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(new URL("https://test-bucket.s3.amazonaws.com/test-key?signature=123"));
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        String url = s3StorageService.generatePresignedUrl("test-key");

        assertThat(url).isEqualTo("https://test-bucket.s3.amazonaws.com/test-key?signature=123");
    }
}
