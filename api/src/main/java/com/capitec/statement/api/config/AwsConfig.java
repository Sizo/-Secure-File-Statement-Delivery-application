package com.capitec.statement.api.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.aws")
public class AwsConfig {

    private String region = "us-east-1";
    private S3Properties s3 = new S3Properties();

    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public S3Properties getS3() { return s3; }
    public void setS3(S3Properties s3) { this.s3 = s3; }

    public static class S3Properties {
        private String bucket;
        private String endpoint;
        private int presignDurationMinutes = 15;

        public String getBucket() { return bucket; }
        public void setBucket(String bucket) { this.bucket = bucket; }
        public String getEndpoint() { return endpoint; }
        public void setEndpoint(String endpoint) { this.endpoint = endpoint; }
        public int getPresignDurationMinutes() { return presignDurationMinutes; }
        public void setPresignDurationMinutes(int presignDurationMinutes) { this.presignDurationMinutes = presignDurationMinutes; }
        public Duration getPresignDuration() { return Duration.ofMinutes(presignDurationMinutes); }
    }

    @Bean
    public S3Client s3Client(Environment environment) {
        S3ClientBuilder builder = S3Client.builder().region(Region.of(region));
        
        if (environment.acceptsProfiles(Profiles.of("local"))) {
            builder.endpointOverride(URI.create(s3.getEndpoint()))
                   .forcePathStyle(true)
                   .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        
        return builder.build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner s3Presigner(Environment environment) {
        S3Presigner.Builder builder = S3Presigner.builder().region(Region.of(region));
        
        if (environment.acceptsProfiles(Profiles.of("local"))) {
            builder.endpointOverride(URI.create(s3.getEndpoint()))
                   .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")));
        } else {
            builder.credentialsProvider(DefaultCredentialsProvider.create());
        }
        
        return builder.build();
    }
}
