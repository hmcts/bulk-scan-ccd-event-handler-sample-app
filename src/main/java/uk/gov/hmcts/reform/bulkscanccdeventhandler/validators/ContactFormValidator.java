package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ContactFormValidator extends OcrFormValidator {

    @Override
    protected List<String> getMandatoryFields() {
        return Arrays.asList(
            OcrFieldNames.ADDRESS_LINE_1,
            OcrFieldNames.EMAIL,
            OcrFieldNames.POST_CODE,
            OcrFieldNames.COUNTRY,
            OcrFieldNames.CONTACT_NUMBER
        );
    }

    @Override
    protected List<String> getWarningFields() {
        return Collections.singletonList(
            OcrFieldNames.ADDRESS_LINE_2
        );
    }

    @Override
    protected List<String> doAdditionalValidations(List<OcrDataField> ocrData) {
        List<String> errors = new ArrayList<>();

        String contactNumber = OcrFormValidationHelper.findOcrFormFieldValue(OcrFieldNames.CONTACT_NUMBER, ocrData);
        String email = OcrFormValidationHelper.findOcrFormFieldValue(OcrFieldNames.EMAIL, ocrData);

        if (!OcrFormValidationHelper.isValidPhoneNumber(contactNumber)) {
            errors.add(OcrFieldNames.CONTACT_NUMBER);
        }

        if (!OcrFormValidationHelper.isValidEmailAddress(email)) {
            errors.add(OcrFieldNames.EMAIL);
        }
        return errors;
    }
}
