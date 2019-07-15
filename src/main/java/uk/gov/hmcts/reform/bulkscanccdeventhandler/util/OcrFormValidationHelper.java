package uk.gov.hmcts.reform.bulkscanccdeventhandler.util;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class OcrFormValidationHelper {

    private OcrFormValidationHelper() {
        // util class
    }

    public static boolean isValidPhoneNumber(String value) {
        return Pattern.compile("\\d{10}").matcher(value).matches();
    }

    public static boolean isValidEmailAddress(String value) {
        String pattern = "^[A-Za-z0-9+_.-]+@(.+)\\.(.+)$";
        Matcher matcher = Pattern.compile(pattern).matcher(value);
        return matcher.matches();
    }

    public static String findOcrFormFieldValue(String fieldName, List<OcrDataField> ocrData) {
        return ocrData
            .stream()
            .filter(e -> fieldName.equals(e.key))
            .findFirst()
            .map(v -> v.value)
            .orElse(null);
    }
}
