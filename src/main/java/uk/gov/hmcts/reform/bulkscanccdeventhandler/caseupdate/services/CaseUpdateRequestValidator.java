package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Component
public class CaseUpdateRequestValidator {
    public void assertIsValid(CaseUpdateRequest caseUpdateRequest) {
        if (caseUpdateRequest.caseUpdateDetails != null
            && isEmpty(caseUpdateRequest.caseUpdateDetails.scannedDocuments)) {
            throw new InvalidCaseUpdateRequestException(
                singletonList("Scanned documents cannot be empty")
            );
        }
    }
}
