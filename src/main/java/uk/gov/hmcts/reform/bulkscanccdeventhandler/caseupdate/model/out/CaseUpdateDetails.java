package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

public class CaseUpdateDetails {

    @JsonProperty("case_data")
    public final SampleCase caseData;

    // region constructor
    public CaseUpdateDetails(SampleCase caseData) {
        this.caseData = caseData;
    }
    // endregion
}
