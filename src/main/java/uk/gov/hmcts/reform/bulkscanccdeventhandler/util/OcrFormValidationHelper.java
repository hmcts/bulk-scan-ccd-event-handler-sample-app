package uk.gov.hmcts.reform.bulkscanccdeventhandler.util;

import org.apache.commons.validator.routines.EmailValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;

import java.util.List;
import java.util.regex.Pattern;

import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.isBlank;

public final class OcrFormValidationHelper {

    private OcrFormValidationHelper() {
        // util class
    }

    public static List<String> findBlankFields(List<String> fieldNames, List<OcrDataField> ocrData) {
        return fieldNames
            .stream()
            .filter(field -> isBlank(OcrFormValidationHelper.findOcrFormFieldValue(field, ocrData)))
            .collect(toList());
    }

    public static String findOcrFormFieldValue(String fieldName, List<OcrDataField> ocrData) {
        return ocrData
            .stream()
            .filter(e -> fieldName.equals(e.key))
            .findFirst()
            .map(v -> v.value)
            .orElse(null);
    }

    public static boolean isValidEmailAddress(String email) {
        return EmailValidator.getInstance().isValid(email);
    }

    public static boolean isValidPhoneNumber(String phone) {
        return Pattern.compile("\\d{10}").matcher(phone).matches();
    }

    public static List<String> getOcrFieldNames(List<OcrDataField> ocrData) {
        return ocrData
            .stream()
            .map(field -> field.key)
            .collect(toList());
    }
}
