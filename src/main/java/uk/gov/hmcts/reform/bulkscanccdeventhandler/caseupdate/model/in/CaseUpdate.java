package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.ExceptionRecord;

public class CaseUpdate {

    public final ExceptionRecord exceptionRecord;
    public final CaseUpdateDetailsRequest caseUpdateDetails;
    public final CaseDetails caseDetails;

    /**
     * Used for 2 case update endpoints:
     * <ul>
     *     <li>Exception record case update</li>
     *     <li>Auto case update</li>
     * </ul>
     * <i>case_update_details</i> element is added for Auto Case update request.
     * <i>exception_record</i> element can be removed after moving to the Auto case update endpoint.
     */
    public CaseUpdate(
        @JsonProperty("exception_record") ExceptionRecord exceptionRecord,
        @JsonProperty("case_update_details") CaseUpdateDetailsRequest caseUpdateDetails,
        @JsonProperty("case_details") CaseDetails caseDetails
    ) {
        this.exceptionRecord = exceptionRecord;
        this.caseUpdateDetails = caseUpdateDetails;
        this.caseDetails = caseDetails;
    }
}
