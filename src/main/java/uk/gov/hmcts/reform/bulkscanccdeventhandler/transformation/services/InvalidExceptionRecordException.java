package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import java.util.List;

public class InvalidExceptionRecordException extends RuntimeException {

    public final List<String> errors;

    public InvalidExceptionRecordException(List<String> errors) {
        super(String.join(",", errors));
        this.errors = errors;
    }
}
