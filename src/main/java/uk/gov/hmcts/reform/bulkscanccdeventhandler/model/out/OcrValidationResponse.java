package uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class OcrValidationResponse {

    @JsonProperty("warnings")
    public final List<String> warnings;

    @JsonProperty("errors")
    public final List<String> errors;

    @JsonProperty("status")
    public final ValidationResponseStatus status;

    @JsonCreator
    public OcrValidationResponse(
        List<String> warnings,
        List<String> errors,
        ValidationResponseStatus status
    ) {
        this.warnings = warnings;
        this.errors = errors;
        this.status = status;
    }
}
