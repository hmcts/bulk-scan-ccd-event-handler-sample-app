package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.CONTACT;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.FormType.PERSONAL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.SUCCESS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.WARNINGS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper.getOcrFieldNames;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper.isValidEmailAddress;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper.isValidPhoneNumber;

@Component
public class OcrDataValidator {

    public OcrValidationResult validate(FormType formType, List<OcrDataField> ocrData) {
        List<String> errors = validateMandatoryFields(formType, ocrData);
        List<String> warnings = validateOptionalFields(formType, ocrData);

        return new OcrValidationResult(warnings, errors, getValidationStatus(!errors.isEmpty(), !warnings.isEmpty()));
    }

    private List<String> validateMandatoryFields(FormType formType, List<OcrDataField> ocrData) {
        List<String> mandatoryFields = getMandatoryFieldsForForm(formType);
        List<String> missingFields = OcrFormValidationHelper.findBlankFields(mandatoryFields, ocrData);
        List<String> errors = getErrorMessagesForFields(missingFields);

        if (formType.equals(CONTACT)) {
            String email = OcrFormValidationHelper.findOcrFormFieldValue(EMAIL, ocrData);
            String phone = OcrFormValidationHelper.findOcrFormFieldValue(CONTACT_NUMBER, ocrData);

            if (!errors.contains(EMAIL) && !isValidEmailAddress(email)) {
                errors.add("Invalid email address");
            }
            if (!errors.contains(CONTACT_NUMBER) && !isValidPhoneNumber(phone)) {
                errors.add("Invalid phone number");
            }
        }

        return errors;
    }

    private List<String> validateOptionalFields(FormType formType, List<OcrDataField> ocrData) {
        List<String> optionalFields = getOptionalFieldsForForm(formType);
        List<String> ocrInputFields = getOcrFieldNames(ocrData);

        List<String> missingFields = findMissingFields(optionalFields, ocrInputFields);
        return getErrorMessagesForFields(missingFields);
    }

    private List<String> getErrorMessagesForFields(List<String> missingFields) {
        return missingFields
            .stream()
            .map(field -> String.format("%s is empty", field))
            .collect(Collectors.toList());
    }

    private List<String> findMissingFields(List<String> optionalFields, List<String> ocrInputFields) {
        return optionalFields.stream()
            .filter(item -> !ocrInputFields.contains(item))
            .collect(Collectors.toList());
    }

    private List<String> getMandatoryFieldsForForm(FormType formType) {
        if (formType.equals(CONTACT)) {
            return asList(
                OcrFieldNames.ADDRESS_LINE_1,
                EMAIL,
                OcrFieldNames.POST_CODE,
                OcrFieldNames.COUNTRY,
                CONTACT_NUMBER
            );

        } else if (formType.equals(PERSONAL)) {
            return Arrays.asList(
                OcrFieldNames.FIRST_NAME,
                OcrFieldNames.LAST_NAME
            );
        }
        return emptyList();
    }

    private List<String> getOptionalFieldsForForm(FormType formType) {
        if (formType.equals(CONTACT)) {
            return asList(
                OcrFieldNames.ADDRESS_LINE_1,
                EMAIL,
                OcrFieldNames.POST_CODE,
                OcrFieldNames.COUNTRY,
                CONTACT_NUMBER
            );
        } else if (formType.equals(PERSONAL)) {
            return singletonList(OcrFieldNames.DATE_OF_BIRTH);
        }
        return emptyList();
    }

    private ValidationStatus getValidationStatus(boolean errorsExist, boolean warningsExist) {
        if (errorsExist) {
            return ERRORS;
        }

        if (warningsExist) {
            return WARNINGS;
        }
        return SUCCESS;
    }

}
