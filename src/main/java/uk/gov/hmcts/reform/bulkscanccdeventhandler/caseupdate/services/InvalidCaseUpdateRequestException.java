package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import java.util.List;

public class InvalidCaseUpdateRequestException extends RuntimeException {

    private final List<String> errors;

    public InvalidCaseUpdateRequestException(List<String> errors) {
        super("Validation errors: " + String.join(", ", errors));
        this.errors = errors;
    }

    public List<String> getErrors() {
        return errors;
    }
}
