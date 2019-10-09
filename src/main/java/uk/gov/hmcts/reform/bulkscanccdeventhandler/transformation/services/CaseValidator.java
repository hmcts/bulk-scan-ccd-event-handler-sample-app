package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import com.google.common.base.Strings;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SampleCase;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.ValidationHelper.isValidEmailAddress;

@Component
public class CaseValidator {

    public List<String> getWarnings(SampleCase theCase) {
        return Strings.isNullOrEmpty(theCase.email)
            ? singletonList("'email' is empty")
            : getEmailValidationResult(theCase);
    }

    private List<String> getEmailValidationResult(SampleCase theCase) {
        return isValidEmailAddress(theCase.email)
            ? emptyList()
            : singletonList("'email' is invalid " + theCase.email);
    }
}
