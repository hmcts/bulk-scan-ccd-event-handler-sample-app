package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;

public class PersonalFormValidator extends OcrFormValidator {
    @Override
    protected List<String> getMandatoryFields() {
        return Arrays.asList(
            OcrFieldNames.FIRST_NAME,
            OcrFieldNames.LAST_NAME
        );
    }

    @Override
    protected List<String> getWarningFields() {
        return Collections.singletonList(
            OcrFieldNames.DATE_OF_BIRTH
        );
    }

    @Override
    protected List<String> doAdditionalValidations(List<OcrDataField> ocrData) {
        return emptyList();
    }
}
