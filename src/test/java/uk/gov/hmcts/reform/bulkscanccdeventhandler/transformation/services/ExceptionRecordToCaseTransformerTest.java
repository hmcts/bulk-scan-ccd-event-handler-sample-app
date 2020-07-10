package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.getSampleInputDocument;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer.CASE_TYPE_ID;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer.EVENT_ID;

@ExtendWith(MockitoExtension.class)
public class ExceptionRecordToCaseTransformerTest {

    @Mock private DocumentMapper documentMapper;
    @Mock private AddressExtractor addressExtractor;
    @Mock private ExceptionRecordValidator exceptionRecordValidator;
    @Mock private CaseValidator caseValidator;

    @Mock private Item<ScannedDocument> doc1;
    @Mock private Item<ScannedDocument> doc2;
    @Mock private Address address;

    private ExceptionRecordToCaseTransformer service;

    @BeforeEach
    public void setUp() {
        this.service =
            new ExceptionRecordToCaseTransformer(
                documentMapper,
                addressExtractor,
                exceptionRecordValidator,
                caseValidator
            );
    }

    @Test
    public void should_map_exception_record_to_a_case() {
        // given
        ExceptionRecord er = new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            "er-form-type",
            JourneyClassification.NEW_APPLICATION,
            now(),
            now(),
            asList(
                getSampleInputDocument("1"),
                getSampleInputDocument("2")
            ),
            asList(
                new OcrDataField(OcrFieldNames.FIRST_NAME, "John"),
                new OcrDataField(OcrFieldNames.LAST_NAME, "Smith")
            ),
            null,
            null,
            null,
            null
        );

        // and
        given(addressExtractor.extractFrom(er.ocrDataFields)).willReturn(address);
        given(documentMapper.toCaseDoc(er.scannedDocuments.get(0), er.id)).willReturn(doc1);
        given(documentMapper.toCaseDoc(er.scannedDocuments.get(1), er.id)).willReturn(doc2);
        given(caseValidator.getWarnings(any())).willReturn(asList("w1", "w2"));
        // when
        SuccessfulTransformationResponse result = service.toCase(er);

        // then

        assertSoftly(softly -> {
            softly.assertThat(result.warnings).containsExactly("w1", "w2");

            softly.assertThat(result.caseCreationDetails.caseTypeId).isEqualTo(CASE_TYPE_ID);
            softly.assertThat(result.caseCreationDetails.eventId).isEqualTo(EVENT_ID);

            softly.assertThat(result.caseCreationDetails.caseData.firstName).isEqualTo("John");
            softly.assertThat(result.caseCreationDetails.caseData.lastName).isEqualTo("Smith");
            softly.assertThat(result.caseCreationDetails.caseData.address).isEqualTo(address);
            softly.assertThat(result.caseCreationDetails.caseData.scannedDocuments)
                .containsExactlyElementsOf(
                    asList(
                        doc1,
                        doc2
                    )
                );
        });
    }

    @Test
    public void should_validate_exception_record() {
        // given
        doThrow(new InvalidExceptionRecordException(asList("error1", "error2")))
            .when(exceptionRecordValidator).assertIsValid(any());

        // when
        Throwable exc = catchThrowable(
            () -> service.toCase(mock(ExceptionRecord.class))
        );

        // then
        assertThat(exc)
            .isInstanceOf(InvalidExceptionRecordException.class)
            .hasMessageContaining("error1")
            .hasMessageContaining("error2");
    }
}
