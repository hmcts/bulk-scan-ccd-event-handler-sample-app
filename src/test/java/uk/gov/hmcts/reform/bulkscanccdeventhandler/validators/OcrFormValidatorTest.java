package uk.gov.hmcts.reform.bulkscanccdeventhandler.validators;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.util.OcrFormValidationHelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.ERRORS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.SUCCESS;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationResponseStatus.WARNINGS;

public class OcrFormValidatorTest {

    private OcrFormValidator validator = new TestFormValidator();

    @Test
    void should_return_validation_errors() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField("name", "test"),
            new OcrDataField("title", "Mr")
        );

        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull();

        assertThat(result.errors)
            .isNotEmpty()
            .hasSize(1)
            .contains("address");

        assertThat(result.warnings).isEmpty();
        assertThat(result.status).isEqualTo(ERRORS);
    }

    @Test
    void should_validate_and_return_warnings() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField("name", "test"),
            new OcrDataField("address", "1, London")
        );

        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result.errors).isEmpty();
        assertThat(result.warnings)
            .isNotEmpty()
            .hasSize(1)
            .contains("title");

        assertThat(result.status).isEqualTo(WARNINGS);
    }

    @Test
    void should_do_additional_validations_when_all_mandatory_fields_exist() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField("name", "test"),
            new OcrDataField("address", "1, London"),
            new OcrDataField("title", "invalid")
        );


        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull();
        assertThat(result.warnings).isEmpty();
        assertThat(result.errors)
            .isNotEmpty()
            .hasSize(1)
            .contains("title");

        assertThat(result.status).isEqualTo(ERRORS);
    }

    @Test
    void should_return_success_status_when_no_validation_errors_and_warnings_exists() {
        // given
        List<OcrDataField> ocrDataFields = Arrays.asList(
            new OcrDataField("name", "test"),
            new OcrDataField("address", "1, London"),
            new OcrDataField("title", "Mr")
        );

        // when
        OcrValidationResult result = validator.validate(ocrDataFields);

        // then
        assertThat(result).isNotNull().extracting("errors", "warnings", "status")
            .contains(emptyList(), emptyList(), SUCCESS);
    }

    class TestFormValidator extends OcrFormValidator {

        @Override
        protected List<String> getMandatoryFields() {
            return Arrays.asList("name", "address");
        }

        @Override
        protected List<String> getWarningFields() {
            return Collections.singletonList("title");
        }

        @Override
        protected List<String> doAdditionalValidations(List<OcrDataField> ocrData) {
            List<String> errors = new ArrayList<>();

            List<String> validTitleValues = Arrays.asList("Mr", "Mrs", "Ms");
            String title = OcrFormValidationHelper.findOcrFormFieldValue("title", ocrData);
            if (StringUtils.isNotBlank(title) && !validTitleValues.contains(title)) {
                errors.add("title");
            }
            return errors;
        }
    }

}
