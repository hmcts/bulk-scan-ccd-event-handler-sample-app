package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;

public class CaseUpdateRequest {

    public final boolean isAutomatedProcess;

    // exception_record element will be removed after moving to the Auto case update endpoint.
    // case_update_details will be used instead, exception_record is preserved
    // for now in order to prevent breaking changes and offer smooth transition to services
    public final TransformationInput transformationInput;

    public final CaseUpdateDetails caseUpdateDetails;
    public final CaseDetails caseDetails;

    public CaseUpdateRequest(
        @JsonProperty("is_automated_process") boolean isAutomatedProcess,
        @JsonProperty("exception_record") TransformationInput transformationInput,
        @JsonProperty("case_update_details") CaseUpdateDetails caseUpdateDetails,
        @JsonProperty("case_details") CaseDetails caseDetails
    ) {
        this.isAutomatedProcess = isAutomatedProcess;
        this.transformationInput = transformationInput;
        this.caseUpdateDetails = caseUpdateDetails;
        this.caseDetails = caseDetails;
    }
}
