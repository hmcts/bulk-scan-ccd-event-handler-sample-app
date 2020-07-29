package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

import java.util.List;

import static java.util.Collections.emptyList;

@Component
public class UpdatedCaseValidator {

    public List<String> getWarnings(SampleCase theCase) {
        // put any warnings here.
        return emptyList();
    }
}
