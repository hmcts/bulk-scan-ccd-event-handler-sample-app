package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateDetails;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.OcrFieldExtractor.get;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.ValidationHelper.isValidEmailAddress;

@Component
public class CaseUpdateDetailsValidator {

    public List<String> getWarnings(CaseUpdateDetails caseUpdateDetails) {
        String email = get(caseUpdateDetails.ocrDataFields, EMAIL);
        if (email == null) {
            return emptyList();
        } else {
            return isValidEmailAddress(email)
                ? emptyList()
                : singletonList("'email' is invalid " + email);
        }
    }
}
