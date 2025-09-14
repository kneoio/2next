package io.kneo.core.repository.exception.attachment;


import java.util.UUID;

public class FileRetrievalFailureException extends RuntimeException  {


    public FileRetrievalFailureException(String id) {
        super(id);
    }

    public FileRetrievalFailureException(UUID id) {
        super(id.toString());
    }


}
