package com.semantyca.core.repository;

import com.semantyca.core.repository.table.IRepository;
import org.apache.tika.Tika;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.nio.file.Paths;

public class AbstractRepository implements IRepository {
    private final Logger LOGGER = Logger.getLogger(AbstractRepository.class);

    public String detectMimeType(String filePath) {
        Tika tika = new Tika();
        try {
            String detectedMimeType = tika.detect(Paths.get(filePath));
            if (detectedMimeType == null || detectedMimeType.isEmpty()) {
                LOGGER.warnf("Tika could not determine MIME type for file %s. Defaulting to application/octet-stream.", filePath);
                return "application/octet-stream";
            } else {
                return detectedMimeType;
            }
        } catch (IOException e) {
            LOGGER.errorf("Tika could not determine MIME type for file %s. Defaulting to application/octet-stream.", filePath);
            return "application/octet-stream";
        }
    }

}
