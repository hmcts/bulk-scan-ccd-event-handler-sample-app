package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import java.util.List;

public class InvalidCaseUpdateDetailsException extends RuntimeException {

    private final List<String> errors;

    public InvalidCaseUpdateDetailsException(List<String> errors) {
        super("Validation errors: " + String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
