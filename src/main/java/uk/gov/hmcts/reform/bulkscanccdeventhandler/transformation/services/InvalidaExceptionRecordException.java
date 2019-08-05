package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

public class InvalidaExceptionRecordException extends RuntimeException {
    public InvalidaExceptionRecordException(String message) {
        super(message);
    }
}
