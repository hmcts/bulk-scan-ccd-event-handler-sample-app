package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.List;
import java.util.stream.Stream;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.getRequiredFields;

public class TransformationInputValidatorTest {

    private final TransformationInputValidator validator = new TransformationInputValidator();

    @Test
    public void should_return_errors_if_required_ocr_fields_are_missing() {
        // given
        TransformationInput transformationInput =
            transformationInputWithOcr(
                asList(
                    new OcrDataField(CONTACT_NUMBER, "555-555-555"),
                    new OcrDataField(EMAIL, "test@example.com")
                )
            );

        // when
        List<String> errors = validator.getErrors(transformationInput);

        // then
        String[] errorMessages =
            Stream.of(FIRST_NAME, LAST_NAME).map(msg -> "'" + msg + "' is required").toArray(String[]::new);
        assertThat(errors).containsExactlyInAnyOrder(errorMessages);
    }

    @Test
    public void should_not_throw_exception_if_exception_record_is_valid() {
        // given
        TransformationInput transformationInput =
            transformationInputWithOcr(
                getRequiredFields()
                    .stream()
                    .map(req -> new OcrDataField(req, "value"))
                    .collect(toList())
            );

        // when
        Throwable exc = catchThrowable(() -> validator.getErrors(transformationInput));

        // then
        assertThat(exc).isNull();
    }

    private TransformationInput transformationInputWithOcr(List<OcrDataField> ocrData) {
        return new TransformationInput(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            "er-form-type",
            JourneyClassification.NEW_APPLICATION,
            now(),
            now(),
            emptyList(),
            ocrData,
            null,
            false,
            null,
            null
        );
    }
}
