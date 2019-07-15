package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;

public abstract class OcrFormValidator {

    public OcrValidationResult validate(List<OcrDataField> ocrData) {
        List<String> errors = validateFields(getMandatoryFields(), ocrData);
        List<String> warnings = validateFields(getWarningFields(), ocrData);

        if (errors.isEmpty()) {
            errors.addAll(doAdditionalValidations(ocrData));
        }

        return new OcrValidationResult(warnings, errors, getStatus(!errors.isEmpty(), !warnings.isEmpty()));
    }

    protected abstract List<String> getMandatoryFields();

    protected abstract List<String> getWarningFields();

    protected abstract List<String> doAdditionalValidations(List<OcrDataField> ocrData);

    private List<String> validateFields(List<String> fieldNames, List<OcrDataField> ocrData) {
        return fieldNames
            .stream()
            .filter(field -> isBlank(OcrFormValidationHelper.findOcrFormFieldValue(field, ocrData)))
            .collect(toList());
    }

    private ValidationResponseStatus getStatus(boolean errorsExist, boolean warningsExist) {
        if (errorsExist) {
            return ValidationResponseStatus.ERRORS;
        }
        if (warningsExist) {
            return ValidationResponseStatus.WARNINGS;
        }
        return ValidationResponseStatus.SUCCESS;
    }
}
