package com.semantyca.core.service.external.hetzner;

import com.semantyca.core.config.HetznerConfig;
import com.semantyca.core.model.FileMetadata;
import com.semantyca.core.model.cnst.FileStorageType;
import com.semantyca.core.repository.IFileStorage;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.http.SdkHttpClient;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.net.URI;
import java.nio.file.Paths;
import java.time.Duration;

@Named("hetzner")
@ApplicationScoped
public class HetznerStorageService implements IFileStorage {

    private static final Logger LOGGER = LoggerFactory.getLogger(HetznerStorageService.class);
    private final HetznerConfig hetznerConfig;
    private S3Client s3Client;

    @Inject
    public HetznerStorageService(HetznerConfig hetznerConfig) {
        this.hetznerConfig = hetznerConfig;
    }

    @PostConstruct
    public void init() {
        String endpointUrl = "https://" + this.hetznerConfig.getEndpoint();
        LOGGER.info("Initializing Hetzner S3 client with endpoint: {}, bucket: {}", endpointUrl, this.hetznerConfig.getBucketName());
        SdkHttpClient httpClient = UrlConnectionHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(30L))
                .socketTimeout(Duration.ofSeconds(60L))
                .build();
        ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2L))
                .apiCallAttemptTimeout(Duration.ofSeconds(90L))
                .build();
        this.s3Client = S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(this.hetznerConfig.getAccessKey(), this.hetznerConfig.getSecretKey())))
                .region(Region.EU_CENTRAL_1)
                .endpointOverride(URI.create(endpointUrl))
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .forcePathStyle(true)
                .build();
    }

    @Override
    public Uni<FileMetadata> getFileStream(String keyName) {
        return Uni.createFrom().item(() -> {
                    LOGGER.debug("Retrieving file stream for key: {}", keyName);
                    GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                            .bucket(this.hetznerConfig.getBucketName())
                            .key(keyName)
                            .build();
                    ResponseInputStream<GetObjectResponse> responseInputStream =
                            this.s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
                    FileMetadata metadata = new FileMetadata();
                    metadata.setInputStream(responseInputStream);
                    metadata.setMimeType(responseInputStream.response().contentType());
                    metadata.setContentLength(responseInputStream.response().contentLength());
                    metadata.setFileKey(keyName);
                    LOGGER.debug("Stream created for key: {}, size: {} bytes", keyName, responseInputStream.response().contentLength());
                    return metadata;
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onFailure().invoke(throwable -> {
                    LOGGER.error("Error retrieving file stream: {} from Hetzner bucket: {}", keyName, this.hetznerConfig.getBucketName(), throwable);
                })
                .onFailure().recoverWithUni(Uni.createFrom()::failure);
    }

    @Override
    public Uni<Void> uploadFile(String keyName, String fileToUpload, String mimeType) {
        return Uni.createFrom().<Void>item(() -> {
                    LOGGER.info("Uploading file with key: {}", keyName);
                    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                            .bucket(this.hetznerConfig.getBucketName())
                            .key(keyName)
                            .contentType(mimeType)
                            .build();
                    this.s3Client.putObject(putObjectRequest, RequestBody.fromFile(Paths.get(fileToUpload)));
                    LOGGER.info("Successfully uploaded file with key: {}", keyName);
                    return null;
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onFailure().invoke(throwable ->
                        LOGGER.error("Error uploading file to Hetzner. Key: {}, Bucket: {}", keyName, this.hetznerConfig.getBucketName(), throwable))
                .onFailure().recoverWithUni(Uni.createFrom()::failure);
    }


    @Override
    public Uni<FileMetadata> retrieveFile(String key) {
        return getFileStream(key);
    }

    @Override
    public Uni<Void> deleteFile(String keyName) {
        if (keyName == null || keyName.isBlank()) {
            return Uni.createFrom().voidItem();
        }
        return Uni.createFrom().<Void>item(() -> {
                    DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                            .bucket(this.hetznerConfig.getBucketName())
                            .key(keyName)
                            .build();
                    this.s3Client.deleteObject(deleteObjectRequest);
                    return null;
                })
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onFailure().invoke(throwable ->
                        LOGGER.error("Error deleting file from Hetzner. Key: {}, Bucket: {}", keyName, this.hetznerConfig.getBucketName(), throwable))
                .onFailure().recoverWithUni(Uni.createFrom()::failure);
    }

    @Override
    public FileStorageType getStorageType() {
        return FileStorageType.HETZNER;
    }

    public void closeS3Client() {
        if (this.s3Client != null) {
            this.s3Client.close();
        }
    }
}