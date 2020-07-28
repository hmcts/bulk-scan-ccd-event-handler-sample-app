package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class CaseUpdateRequestValidator {
    public void assertIsValid(CaseUpdateRequest caseUpdateRequest) {
        if (!isNotEmpty(caseUpdateRequest.caseUpdateDetails.scannedDocuments)) {
            throw new InvalidCaseUpdateRequestException(
                singletonList("Scanned documents cannot be empty")
            );
        }
    }
}
