package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

public class CaseDetails {

    public final String caseTypeId;
    public final SampleCase caseData;

    public CaseDetails(
        @JsonProperty("case_type_id") String caseTypeId,
        @JsonProperty("case_data") SampleCase caseData
    ) {
        this.caseTypeId = caseTypeId;
        this.caseData = caseData;
    }
}
