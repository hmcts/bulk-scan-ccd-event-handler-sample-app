package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.SUCCESS;

public class ContactFormValidatorTest {
    private OcrFormValidator validator = new ContactFormValidator();

    @Test
    void should_return_errors_when_email_is_invalid() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField(OcrFieldNames.ADDRESS_LINE_1, "test"),
            new OcrDataField(OcrFieldNames.ADDRESS_LINE_2, "test"),
            new OcrDataField(OcrFieldNames.EMAIL, "test"),
            new OcrDataField(OcrFieldNames.POST_CODE, "test"),
            new OcrDataField(OcrFieldNames.COUNTRY, "UK"),
            new OcrDataField(OcrFieldNames.CONTACT_NUMBER, "1234567890")
        );

        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result.warnings).isEmpty();
        assertThat(result.errors)
            .isNotEmpty()
            .hasSize(1)
            .contains(OcrFieldNames.EMAIL);

        assertThat(result.status).isEqualTo(ERRORS);
    }

    @Test
    void should_return_errors_when_phone_number_is_invalid() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField(OcrFieldNames.ADDRESS_LINE_1, "test"),
            new OcrDataField(OcrFieldNames.ADDRESS_LINE_2, "test"),
            new OcrDataField(OcrFieldNames.EMAIL, "test@test.com"),
            new OcrDataField(OcrFieldNames.POST_CODE, "test"),
            new OcrDataField(OcrFieldNames.COUNTRY, "UK"),
            new OcrDataField(OcrFieldNames.CONTACT_NUMBER, "invalid_phone")
        );

        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result.warnings).isEmpty();
        assertThat(result.errors)
            .isNotEmpty()
            .hasSize(1)
            .contains(OcrFieldNames.CONTACT_NUMBER);

        assertThat(result.status).isEqualTo(ERRORS);
    }

    @Test
    void should_return_empty_errors_list_when_ocr_form_data_is_valid() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField(OcrFieldNames.ADDRESS_LINE_1, "test"),
            new OcrDataField(OcrFieldNames.ADDRESS_LINE_2, "test"),
            new OcrDataField(OcrFieldNames.EMAIL, "test@test.com"),
            new OcrDataField(OcrFieldNames.POST_CODE, "test"),
            new OcrDataField(OcrFieldNames.COUNTRY, "UK"),
            new OcrDataField(OcrFieldNames.CONTACT_NUMBER, "1234567890")
        );

        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result.warnings).isEmpty();
        assertThat(result.errors).isEmpty();
        assertThat(result.status).isEqualTo(SUCCESS);
    }
}
