package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;

public class CaseUpdate {

    public final TransformationInput transformationInput;
    public final CaseDetails caseDetails;

    public CaseUpdate(
        @JsonProperty("exception_record") TransformationInput transformationInput,
        @JsonProperty("case_details") CaseDetails caseDetails
    ) {
        this.transformationInput = transformationInput;
        this.caseDetails = caseDetails;
    }
}
