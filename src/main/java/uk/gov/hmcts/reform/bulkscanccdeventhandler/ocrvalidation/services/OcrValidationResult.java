package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus;

import java.util.List;

public class OcrValidationResult {

    public final List<String> warnings;
    public final List<String> errors;
    public final ValidationStatus status;

    // region constructor
    public OcrValidationResult(
        List<String> warnings,
        List<String> errors,
        ValidationStatus status
    ) {
        this.warnings = warnings;
        this.errors = errors;
        this.status = status;
    }
    // end region
}
