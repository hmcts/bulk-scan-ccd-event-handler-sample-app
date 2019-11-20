package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SuccessfulUpdateResponse {

    @JsonProperty("case_update_details")
    public final CaseUpdateDetails caseUpdateDetails;

    @JsonProperty("warnings")
    public final List<String> warnings;

    // region constructor
    public SuccessfulUpdateResponse(
        CaseUpdateDetails caseUpdateDetails,
        List<String> warnings
    ) {
        this.caseUpdateDetails = caseUpdateDetails;
        this.warnings = warnings;
    }
    // endregion
}
