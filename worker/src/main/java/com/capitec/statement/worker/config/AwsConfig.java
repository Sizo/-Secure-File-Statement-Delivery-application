package com.capitec.statement.worker.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClientBuilder;

import java.net.URI;

@Configuration
public class AwsConfig {

    @Value("${app.aws.region:us-east-1}")
    private String region;

    @Value("${app.aws.s3.endpoint:#{null}}")
    private String s3Endpoint;

    @Value("${spring.cloud.aws.sqs.endpoint:#{null}}")
    private String sqsEndpoint;

    @Bean
    public S3Client s3Client() {
        S3ClientBuilder builder = S3Client.builder()
                .region(Region.of(region));
                // .credentialsProvider(DefaultCredentialsProvider.create());

        if (s3Endpoint != null && !s3Endpoint.isBlank()) {
            builder.endpointOverride(URI.create(s3Endpoint))
                   .forcePathStyle(true);
        }

        return builder.build();
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        SqsAsyncClientBuilder builder = SqsAsyncClient.builder()
                .region(Region.of(region));
                // .credentialsProvider(DefaultCredentialsProvider.create());

        if (sqsEndpoint != null && !sqsEndpoint.isBlank()) {
            builder.endpointOverride(URI.create(sqsEndpoint));
        }

        return builder.build();
    }
}