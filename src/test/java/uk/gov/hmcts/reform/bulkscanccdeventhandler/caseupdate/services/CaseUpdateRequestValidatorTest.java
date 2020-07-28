package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.caseDetails;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.caseUpdateDetails;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.getSampleInputDocument;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.sampleCase;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.transformationInput;

public class CaseUpdateRequestValidatorTest {

    private final CaseUpdateRequestValidator validator = new CaseUpdateRequestValidator();

    @Test
    public void should_throw_exception_if_required_ocr_fields_are_missing() {
        // given
        final SampleCase originalCase = sampleCase(emptyList());

        final TransformationInput transformationInput = transformationInput(emptyList());

        final CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(emptyList(), emptyList());

        final CaseUpdateRequest caseUpdateRequest = new CaseUpdateRequest(
            false,
            transformationInput,
            caseUpdateDetails,
            caseDetails(originalCase)
        );

        // when
        InvalidCaseUpdateRequestException exc =
            catchThrowableOfType(
                () -> validator.assertIsValid(caseUpdateRequest),
                InvalidCaseUpdateRequestException.class
            );

        // then
        assertThat(exc)
            .isInstanceOf(InvalidCaseUpdateRequestException.class)
            .hasMessageContaining("Scanned documents cannot be empty");
    }

    @Test
    public void should_not_throw_exception_if_case_update_details_not_present() {
        // given
        final SampleCase originalCase = sampleCase(emptyList());

        final TransformationInput transformationInput = transformationInput(emptyList());

        final CaseUpdateRequest caseUpdateRequest = new CaseUpdateRequest(
            false,
            transformationInput,
            null,
            caseDetails(originalCase)
        );

        // when
        // then
        assertThatCode(() -> validator.assertIsValid(caseUpdateRequest)).doesNotThrowAnyException();
    }

    @Test
    public void should_not_throw_exception_if_exception_record_is_valid() {
        // given
        final SampleCase originalCase = sampleCase(emptyList());

        final TransformationInput transformationInput = transformationInput(emptyList());

        final CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            singletonList(getSampleInputDocument()),
            emptyList()
        );

        final CaseUpdateRequest caseUpdateRequest = new CaseUpdateRequest(
            false,
            transformationInput,
            caseUpdateDetails,
            caseDetails(originalCase)
        );

        // when
        // then
        assertThatCode(() -> validator.assertIsValid(caseUpdateRequest)).doesNotThrowAnyException();
    }
}
