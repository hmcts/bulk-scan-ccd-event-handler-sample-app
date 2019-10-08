package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SampleCase;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class CaseValidator {

    public List<String> getWarnings(SampleCase theCase) {
        //return Strings.isNullOrEmpty(theCase.email)
        //    ? singletonList("'email' is empty")
        //    : emptyList();
        return emptyList();
    }
}
