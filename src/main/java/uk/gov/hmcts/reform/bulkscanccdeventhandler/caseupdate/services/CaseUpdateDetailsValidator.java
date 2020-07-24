package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.InvalidExceptionRecordException;

import static java.util.Collections.singletonList;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

@Component
public class CaseUpdateDetailsValidator {
    public void assertIsValid(CaseUpdateRequest caseUpdateRequest) {
        if (!isNotEmpty(caseUpdateRequest.caseUpdateDetails.scannedDocuments)) {
            throw new InvalidExceptionRecordException(
                singletonList("Scanned documents cannot be empty")
            );
        }
    }
}
