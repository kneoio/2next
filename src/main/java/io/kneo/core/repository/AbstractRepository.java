package io.kneo.core.repository;

import io.kneo.core.repository.table.IRepository;
import org.apache.tika.Tika;
import java.io.IOException;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbstractRepository implements IRepository {
    protected final Logger LOGGER = LoggerFactory.getLogger(AbstractRepository.class);

    protected String detectMimeType(String filePath) {
        Tika tika = new Tika();
        try {
            String detectedMimeType = tika.detect(Paths.get(filePath));
            if (detectedMimeType == null || detectedMimeType.isEmpty()) {
                LOGGER.warn("Tika could not determine MIME type for file {}. Defaulting to application/octet-stream.", filePath);
                return "application/octet-stream";
            } else {
                return detectedMimeType;
            }
        } catch (IOException e) {
            LOGGER.error("Tika could not determine MIME type for file {}. Defaulting to application/octet-stream.", filePath);
            return "application/octet-stream";
        }
    }

}
