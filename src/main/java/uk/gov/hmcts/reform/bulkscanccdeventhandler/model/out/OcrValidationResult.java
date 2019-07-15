package uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out;

import java.util.List;

public class OcrValidationResult {

    public final List<String> warnings;
    public final List<String> errors;
    public final ValidationResponseStatus status;

    // region constructor
    public OcrValidationResult(
        List<String> warnings,
        List<String> errors,
        ValidationResponseStatus status
    ) {
        this.warnings = warnings;
        this.errors = errors;
        this.status = status;
    }
    // end region
}
