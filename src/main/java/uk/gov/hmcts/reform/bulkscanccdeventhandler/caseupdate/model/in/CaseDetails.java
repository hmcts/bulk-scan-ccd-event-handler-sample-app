package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

public class CaseDetails {

    //id is for logging
    public final String id;
    public final String caseTypeId;
    public final SampleCase caseData;

    public CaseDetails(
        @JsonProperty("id") String id,
        @JsonProperty("case_type_id") String caseTypeId,
        @JsonProperty("case_data") SampleCase caseData
    ) {
        this.id = id;
        this.caseTypeId = caseTypeId;
        this.caseData = caseData;
    }
}
