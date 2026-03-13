package com.semantyca.core.repository;


import com.semantyca.core.model.FileMetadata;
import com.semantyca.core.model.cnst.FileStorageType;
import io.smallrye.mutiny.Uni;

public interface IFileStorage {

    Uni<FileMetadata> getFileStream(String keyName);

    Uni<Void> uploadFile(String keyName, String fileToUpload, String mimeType);

    Uni<Void> deleteFile(String keyName);

    FileStorageType getStorageType();

    @Deprecated
    Uni<FileMetadata> retrieveFile(String key);
}