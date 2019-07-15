package uk.gov.hmcts.reform.bulkscanccdeventhandler.util;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;

import java.util.List;

public final class OcrFormValidationHelper {

    private OcrFormValidationHelper() {
        // util class
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
