package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CaseUpdateRequest {

    public final boolean isAutomatedProcess;
    public final CaseUpdateDetails caseUpdateDetails;
    public final CaseDetails caseDetails;

    public CaseUpdateRequest(
        @JsonProperty("is_automated_process") boolean isAutomatedProcess,
        @JsonProperty("case_update_details") CaseUpdateDetails caseUpdateDetails,
        @JsonProperty("case_details") CaseDetails caseDetails
    ) {
        this.isAutomatedProcess = isAutomatedProcess;
        this.caseUpdateDetails = caseUpdateDetails;
        this.caseDetails = caseDetails;
    }
}
