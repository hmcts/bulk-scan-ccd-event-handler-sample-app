package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.ExceptionRecord;

public class CaseUpdate {

    public final ExceptionRecord exceptionRecord;
    public final CaseDetails caseDetails;

    public CaseUpdate(
        @JsonProperty("exception_record") ExceptionRecord exceptionRecord,
        @JsonProperty("case_details") CaseDetails caseDetails
    ) {
        this.exceptionRecord = exceptionRecord;
        this.caseDetails = caseDetails;
    }
}
