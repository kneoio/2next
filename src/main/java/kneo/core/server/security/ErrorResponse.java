package kneo.core.server.security;

public class ErrorResponse {
    private final int status;
    private final String code;
    private final String message;
    private final String details;
    private final boolean logError;

    public enum ErrorCode {
        INVALID_REQUEST(400, "Invalid request parameters", "The request parameters are invalid or missing", false),
        DOCUMENT_NOT_FOUND(404, "Document not found", "The requested document could not be found in the system", false),
        USER_NOT_FOUND(403, "User not found or unauthorized", "The user does not exist or lacks necessary permissions", false),
        DOCUMENT_ACCESS_DENIED(404, "Document access denied", "You don't have permission to modify this document", false),
        CONNECTION_ERROR(500, "API server connection error", "Failed to establish connection with the server", true),
        DATABASE_ERROR(500, "Database operation failed", "An error occurred while processing the database operation", true),
        RESOURCE_NOT_AVAILABLE(500, "Resource not available", "The requested resource is not available in the system", true),
        UNKNOWN_ERROR(500, "Internal server error", "An unexpected error occurred", true);

        private final int status;
        private final String message;
        private final String details;
        private final boolean logError;

        ErrorCode(int status, String message, String details, boolean logError) {
            this.status = status;
            this.message = message;
            this.details = details;
            this.logError = logError;
        }
    }

    public ErrorResponse(ErrorCode code) {
        this.status = code.status;
        this.code = code.name();
        this.message = code.message;
        this.details = code.details;
        this.logError = code.logError;
    }

    public int getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getDetails() {
        return details;
    }

    public boolean isLogError() {
        return logError;
    }
}
