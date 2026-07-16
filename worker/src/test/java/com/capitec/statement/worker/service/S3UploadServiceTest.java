package com.capitec.statement.worker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class S3UploadServiceTest {

    @Mock
    private S3Client s3Client;

    private S3UploadService s3UploadService;

    @BeforeEach
    void setUp() {
        s3UploadService = new S3UploadService(s3Client, "test-bucket");
    }

    @Test
    void testUploadStatement() {
        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        s3UploadService.uploadStatement("test-key.pdf", "test content".getBytes());

        ArgumentCaptor<PutObjectRequest> captor = ArgumentCaptor.forClass(PutObjectRequest.class);
        verify(s3Client).putObject(captor.capture(), any(RequestBody.class));

        assertEquals("test-bucket", captor.getValue().bucket());
        assertEquals("test-key.pdf", captor.getValue().key());
        assertEquals("application/pdf", captor.getValue().contentType());
    }

    @Test
    void testGenerateS3Key() {
        UUID id = UUID.randomUUID();
        String key = s3UploadService.generateS3Key("cust-123", "2023-05", id);

        assertEquals("statements/cust-123/2023/05/" + id + ".pdf", key);
    }
}