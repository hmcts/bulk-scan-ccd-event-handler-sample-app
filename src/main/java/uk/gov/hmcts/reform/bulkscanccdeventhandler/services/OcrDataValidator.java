package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.validators.ContactFormValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.validators.OcrFormValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.validators.PersonalFormValidator;

import java.util.List;

import static java.util.Collections.emptyList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.CONTACT;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.PERSONAL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.SUCCESS;

public final class OcrDataValidator {

    private static OcrFormValidator contactFormValidator = new ContactFormValidator();
    private static OcrFormValidator personalFormValidator = new PersonalFormValidator();

    // prevent instantiation
    private OcrDataValidator() {
    }

    public static OcrValidationResult validate(FormType formType, List<OcrDataField> ocrDataFields) {
        if (formType.equals(CONTACT)) {
            return contactFormValidator.validate(ocrDataFields);
        } else if (formType.equals(PERSONAL)) {
            return personalFormValidator.validate(ocrDataFields);
        }
        // this is unreachable as the form type is validated in the controller
        return new OcrValidationResult(emptyList(), emptyList(), SUCCESS);
    }
}
