package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;

class CaseUpdateDetailsValidatorTest {

    private CaseUpdateDetailsValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CaseUpdateDetailsValidator();
    }

    @Test
    public void should_return_warning_when_email_is_blank() {
        assertThat(validator.getWarnings(detailsWithEmail("")))
            .containsExactly("invalid email ''");
    }

    @Test
    public void should_return_warning_when_email_is_invalid() {
        assertThat(validator.getWarnings(detailsWithEmail("invalidemail")))
            .containsExactly("invalid email 'invalidemail'");
    }

    @Test
    void should_return_empty_array_if_email_is_valid() {
        assertThat(validator.getWarnings(detailsWithEmail("hello@test.com"))).isEmpty();
    }

    @Test
    void should_return_empty_array_if_email_is_null() {
        assertThat(validator.getWarnings(detailsWithEmail(null))).isEmpty();
    }

    private CaseUpdateDetails detailsWithEmail(String email) {
        return new CaseUpdateDetails(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            email == null ? emptyList() : singletonList(new OcrDataField("email", email)),
            null
        );
    }
}
