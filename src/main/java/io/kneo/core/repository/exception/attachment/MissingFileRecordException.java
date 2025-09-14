package io.kneo.core.repository.exception.attachment;


import java.util.UUID;

public class MissingFileRecordException extends RuntimeException  {


    public MissingFileRecordException(String id) {
        super(id);
    }

    public MissingFileRecordException(UUID id) {
        super(id.toString());
    }


}
