package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.SUCCESS;

public class PersonalFormValidatorTest {

    OcrFormValidator validator = new PersonalFormValidator();

    @Test
    void should_return_success_status_when_no_additional_validations_required() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField(OcrFieldNames.FIRST_NAME, "a"),
            new OcrDataField(OcrFieldNames.LAST_NAME, "b"),
            // no additional validation added for date_of_birth, so it can be any value
            new OcrDataField(OcrFieldNames.DATE_OF_BIRTH, "dob")
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
