package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper;

import java.util.List;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.SUCCESS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus.WARNINGS;

public abstract class OcrFormValidator {

    public OcrValidationResult validate(List<OcrDataField> ocrData) {
        List<String> errors = findBlankFields(getMandatoryFields(), ocrData);
        List<String> warnings = findBlankFields(getWarningFields(), ocrData);

        errors.addAll(doAdditionalValidations(ocrData));

        return new OcrValidationResult(warnings, errors, getStatus(!errors.isEmpty(), !warnings.isEmpty()));
    }

    protected abstract List<String> getMandatoryFields();

    protected abstract List<String> getWarningFields();

    protected abstract List<String> doAdditionalValidations(List<OcrDataField> ocrData);

    private List<String> findBlankFields(List<String> fieldNames, List<OcrDataField> ocrData) {
        return fieldNames
            .stream()
            .filter(field -> isBlank(OcrFormValidationHelper.findOcrFormFieldValue(field, ocrData)))
            .collect(toList());
    }

    private ValidationStatus getStatus(boolean errorsExist, boolean warningsExist) {
        if (errorsExist) {
            return ERRORS;
        }

        if (warningsExist) {
            return WARNINGS;
        }
        return SUCCESS;
    }
}
