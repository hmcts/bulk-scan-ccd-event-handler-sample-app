package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

public class CaseUpdateDetails {

    @JsonProperty("case_type_id")
    public final String caseTypeId;

    @JsonProperty("event_id")
    public final String eventId;

    @JsonProperty("case_data")
    public final SampleCase caseData;

    // region constructor
    public CaseUpdateDetails(
        String caseTypeId,
        String eventId,
        SampleCase caseData
    ) {
        this.caseTypeId = caseTypeId;
        this.eventId = eventId;
        this.caseData = caseData;
    }
    // endregion
}
