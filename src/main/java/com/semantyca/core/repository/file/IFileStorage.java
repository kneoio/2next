package com.semantyca.core.repository.file;


import com.semantyca.core.model.FileMetadata;
import com.semantyca.core.model.cnst.FileStorageType;
import io.smallrye.mutiny.Uni;

import java.util.UUID;

public interface IFileStorage {

    Uni<String> storeFile(String key, String filePath, String mimeType, String tableName, UUID id);

    Uni<String> storeFile(String key, byte[] fileContent, String mimeType, String tableName, UUID id);

    Uni<FileMetadata> retrieveFile(String key);

    Uni<Void> deleteFile(String key);

    FileStorageType getStorageType();
}