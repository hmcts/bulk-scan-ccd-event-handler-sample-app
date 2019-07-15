package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.validators.ContactFormValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.validators.OcrFormValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.validators.PersonalFormValidator;

import java.util.List;

public final class OcrDataValidator {

    private static OcrFormValidator CONTACT_FORM_VALIDATOR = new ContactFormValidator();
    private static OcrFormValidator PERSONAL_FORM_VALIDATOR = new PersonalFormValidator();

    // prevent instantiation
    private OcrDataValidator() {
    }

    public static OcrValidationResult validate(FormType formType, List<OcrDataField> ocrDataFields) {
        OcrFormValidator formValidator = getFormValidator(formType);
        return formValidator.validate(ocrDataFields);
    }

    @SuppressWarnings("checkstyle:MissingSwitchDefault")
    private static OcrFormValidator getFormValidator(FormType formType) {
        OcrFormValidator validator = null;
        switch (formType) {
            case CONTACT:
                validator = CONTACT_FORM_VALIDATOR;
                break;
            case PERSONAL:
                validator = PERSONAL_FORM_VALIDATOR;
                break;
        }
        // 'default' is unreachable as the form type is validated in the controller.
        return validator;
    }

}
