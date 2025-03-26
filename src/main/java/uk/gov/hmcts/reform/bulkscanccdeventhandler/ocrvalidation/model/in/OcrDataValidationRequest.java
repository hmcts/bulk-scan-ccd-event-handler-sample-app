package uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import jakarta.validation.constraints.NotEmpty;

public class OcrDataValidationRequest {

    @Schema(title = "List of ocr data fields to be validated.", required = true)
    @NotEmpty
    public final List<OcrDataField> ocrDataFields;

    public OcrDataValidationRequest(
        @JsonProperty("ocr_data_fields") List<OcrDataField> ocrDataFields
    ) {
        this.ocrDataFields = ocrDataFields;
    }

    public List<OcrDataField> getOcrDataFields() {
        return ocrDataFields;
    }
}
