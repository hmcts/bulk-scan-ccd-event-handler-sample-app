package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.address;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.caseDetails;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.caseUpdateDetails;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.inputScannedDocuments;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.sampleCase;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.scannedDocuments;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LEGACY_ID;

@ExtendWith(MockitoExtension.class)
class CaseUpdaterTest {

    @Mock private CaseUpdateDetailsValidator caseUpdateDetailsValidator;
    @Mock private AddressExtractor addressExtractor;

    private CaseUpdater caseUpdater;

    @BeforeEach
    void setUp() {
        this.caseUpdater = new CaseUpdater(
            caseUpdateDetailsValidator,
            addressExtractor
        );
    }

    /**
     * should override the address with exception record address data.
     * should return total document list by merging the document list with input and existing case
     *
     */
    @Test
    void should_update_case_data() {
        // given
        Address address = address("-er");

        List<Item<ScannedDocument>> caseScannedDocuments = scannedDocuments();
        SampleCase originalCase = sampleCase(caseScannedDocuments);

        List<InputScannedDoc> inputScannedDocs = inputScannedDocuments();

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocs,
            emptyList()
        );

        given(caseUpdateDetailsValidator.getWarnings(caseUpdateDetails)).willReturn(emptyList());
        given(addressExtractor.extractFrom(any())).willReturn(address);

        // when
        SuccessfulUpdateResponse result =
            caseUpdater.update(
                new CaseUpdateRequest(
                    false,
                    caseUpdateDetails,
                    caseDetails(originalCase)
                )
            );

        // then
        assertThat(result.caseUpdateDetails.caseData.address)
            .isEqualToComparingFieldByField(address);
        assertCaseData(result.caseUpdateDetails.caseData, originalCase);
        assertThat(result.warnings).isEmpty();
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocumentsFromExistingCase(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDocuments
        );
        assertScannedDocumentsFromExceptionRecord(
            result.caseUpdateDetails.caseData.scannedDocuments,
            inputScannedDocs
        );
    }

    @Test
    void should_override_existing_fields_with_new_values() {
        // given
        Address address = address("-er");

        List<Item<ScannedDocument>> caseScannedDocuments = scannedDocuments();
        SampleCase originalCase = sampleCase(caseScannedDocuments);

        List<InputScannedDoc> inputScannedDocs = inputScannedDocuments();

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocs,
            List.of(
                new OcrDataField(LEGACY_ID, "new-legacy-id"),
                new OcrDataField(FIRST_NAME, "new-first-name"),
                new OcrDataField(LAST_NAME, "new-last-name"),
                new OcrDataField(DATE_OF_BIRTH, "new-date-of-birth"),
                new OcrDataField(CONTACT_NUMBER, "new-contact-number"),
                new OcrDataField(EMAIL, "new-email")
            )
        );

        given(caseUpdateDetailsValidator.getWarnings(caseUpdateDetails)).willReturn(emptyList());
        given(addressExtractor.extractFrom(any())).willReturn(address);

        // when
        SuccessfulUpdateResponse result =
            caseUpdater.update(
                new CaseUpdateRequest(
                    false,
                    caseUpdateDetails,
                    caseDetails(originalCase)
                )
            );

        // then
        assertThat(result.caseUpdateDetails.caseData.address)
            .isEqualToComparingFieldByField(address);
        assertThat(result.caseUpdateDetails.caseData.legacyId).isEqualTo("new-legacy-id");
        assertThat(result.caseUpdateDetails.caseData.firstName).isEqualTo("new-first-name");
        assertThat(result.caseUpdateDetails.caseData.lastName).isEqualTo("new-last-name");
        assertThat(result.caseUpdateDetails.caseData.dateOfBirth).isEqualTo("new-date-of-birth");
        assertThat(result.caseUpdateDetails.caseData.contactNumber).isEqualTo("new-contact-number");
        assertThat(result.caseUpdateDetails.caseData.email).isEqualTo("new-email");
        assertThat(result.warnings).isEmpty();
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocumentsFromExistingCase(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDocuments
        );
        assertScannedDocumentsFromExceptionRecord(
            result.caseUpdateDetails.caseData.scannedDocuments,
            inputScannedDocs
        );
    }

    @Test
    void should_override_empty_fields_with_new_values() {
        // given
        Address address = address("-er");

        List<Item<ScannedDocument>> caseScannedDocuments = scannedDocuments();
        SampleCase originalCase = new SampleCase(
            null,
            null,
            null,
            null,
            null,
            null,
            address(""),
            caseScannedDocuments,
            "er-id"
        );

        List<InputScannedDoc> inputScannedDocs = inputScannedDocuments();

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocs,
            List.of(
                new OcrDataField(LEGACY_ID, "new-legacy-id"),
                new OcrDataField(FIRST_NAME, "new-first-name"),
                new OcrDataField(LAST_NAME, "new-last-name"),
                new OcrDataField(DATE_OF_BIRTH, "new-date-of-birth"),
                new OcrDataField(CONTACT_NUMBER, "new-contact-number"),
                new OcrDataField(EMAIL, "new-email")
            )
        );

        given(caseUpdateDetailsValidator.getWarnings(caseUpdateDetails)).willReturn(emptyList());
        given(addressExtractor.extractFrom(any())).willReturn(address);

        // when
        SuccessfulUpdateResponse result =
            caseUpdater.update(
                new CaseUpdateRequest(
                    false,
                    caseUpdateDetails,
                    caseDetails(originalCase)
                )
            );

        // then
        assertThat(result.caseUpdateDetails.caseData.address)
            .isEqualToComparingFieldByField(address);
        assertThat(result.caseUpdateDetails.caseData.legacyId).isEqualTo("new-legacy-id");
        assertThat(result.caseUpdateDetails.caseData.firstName).isEqualTo("new-first-name");
        assertThat(result.caseUpdateDetails.caseData.lastName).isEqualTo("new-last-name");
        assertThat(result.caseUpdateDetails.caseData.dateOfBirth).isEqualTo("new-date-of-birth");
        assertThat(result.caseUpdateDetails.caseData.contactNumber).isEqualTo("new-contact-number");
        assertThat(result.caseUpdateDetails.caseData.email).isEqualTo("new-email");
        assertThat(result.warnings).isEmpty();
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocumentsFromExistingCase(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDocuments
        );
        assertScannedDocumentsFromExceptionRecord(
            result.caseUpdateDetails.caseData.scannedDocuments,
            inputScannedDocs
        );
    }

    @Test
    void should_update_case_if_auto_process_is_false_and_there_are_update_details_warnings() {
        // given
        Address exceptionRecordAddress = address("-er");

        List<Item<ScannedDocument>> caseScannedDocuments = scannedDocuments();
        SampleCase originalCase = sampleCase(caseScannedDocuments);

        List<InputScannedDoc> inputScannedDocs = inputScannedDocuments();

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocs,
            emptyList()
        );

        given(caseUpdateDetailsValidator.getWarnings(caseUpdateDetails)).willReturn(asList("w1", "w2"));
        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);

        // when
        SuccessfulUpdateResponse result =
            caseUpdater.update(
                new CaseUpdateRequest(
                    false,
                    caseUpdateDetails,
                    caseDetails(originalCase)
                )
            );

        // then
        assertThat(result.caseUpdateDetails.caseData.address)
            .isEqualToComparingFieldByField(exceptionRecordAddress);
        assertCaseData(result.caseUpdateDetails.caseData, originalCase);
        assertThat(result.warnings).containsExactlyInAnyOrder("w1", "w2");
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocumentsFromExistingCase(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDocuments
        );
        assertScannedDocumentsFromExceptionRecord(
            result.caseUpdateDetails.caseData.scannedDocuments,
            inputScannedDocs
        );
    }

    @Test
    void should_throw_exception_for_auto_process_is_true_when_there_are_update_details_validation_warnings() {
        // given
        Address exceptionRecordAddress = address("-er");

        List<Item<ScannedDocument>> scannedDocuments = scannedDocuments();
        SampleCase originalCase = sampleCase(scannedDocuments);

        List<InputScannedDoc> inputScannedDocuments = inputScannedDocuments();

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocuments,
            emptyList()
        );

        given(caseUpdateDetailsValidator.getWarnings(caseUpdateDetails)).willReturn(asList("w1", "w2"));
        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);

        // when
        InvalidCaseUpdateDetailsException exc = catchThrowableOfType(
            () -> caseUpdater.update(
                new CaseUpdateRequest(
                    true,
                    caseUpdateDetails,
                    caseDetails(originalCase)
                )
            ),
            InvalidCaseUpdateDetailsException.class
        );

        // then
        assertThat(exc.getErrors()).containsExactlyInAnyOrder("w1", "w2");
    }

    @Test
    void should_not_update_case_data_without_any_documents() {
        // given
        SampleCase originalCase = sampleCase(emptyList());

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(emptyList(), emptyList());

        // when
        IllegalArgumentException exception = catchThrowableOfType(() ->
                caseUpdater.update(
                    new CaseUpdateRequest(
                        false,
                        caseUpdateDetails,
                        caseDetails(originalCase)
                    )
                ),
            IllegalArgumentException.class
        );

        // then
        assertThat(exception).hasMessage("Missing scanned documents");
        verifyNoInteractions(addressExtractor);
    }

    @Test
    void should_throw_for_auto_process_is_true_when_case_update_details_null() {
        // given
        List<Item<ScannedDocument>> scannedDocuments = scannedDocuments();
        SampleCase originalCase = sampleCase(scannedDocuments);

        List<InputScannedDoc> inputScannedDocuments = inputScannedDocuments();

        // when
        IllegalArgumentException exc = catchThrowableOfType(
            () -> caseUpdater.update(
                new CaseUpdateRequest(
                    true,
                    null,
                    caseDetails(originalCase)
                )
            ),
            IllegalArgumentException.class
        );

        // then
        assertThat(exc.getMessage()).isEqualTo("Case update details is required");
    }

    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void should_throw_when_scanned_documents_empty(boolean isAutomatedProcess) {
        // given
        SampleCase originalCase = sampleCase(scannedDocuments());

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(emptyList(), emptyList());

        // when
        IllegalArgumentException exc = catchThrowableOfType(
            () -> caseUpdater.update(
                new CaseUpdateRequest(
                    isAutomatedProcess,
                    caseUpdateDetails,
                    caseDetails(originalCase)
                )
            ),
            IllegalArgumentException.class
        );

        // then
        assertThat(exc.getMessage()).isEqualTo("Missing scanned documents");
    }

    private void assertCaseData(SampleCase actualCaseData, SampleCase expectedCaseData) {
        assertThat(actualCaseData)
            .isEqualToComparingOnlyGivenFields(expectedCaseData,
                "legacyId",
                "firstName",
                "lastName",
                "dateOfBirth",
                "bulkScanCaseReference"
            );
    }

    private void assertScannedDocumentsFromExistingCase(
        List<Item<ScannedDocument>> actualScannedDocuments,
        List<Item<ScannedDocument>> caseScannedDocuments
    ) {
        // Scanned documents in the updated case should contain scanned documents
        // from the existing case and scanned documents from the exception record.
        // Because there is 1 scanned document in the existing case
        // the scanned document at index 0 in the updated case should match
        // this scanned document in the existing case
        assertThat(actualScannedDocuments.get(0).value)
            .isEqualToComparingFieldByField(caseScannedDocuments.get(0).value);
    }

    private void assertScannedDocumentsFromExceptionRecord(
        List<Item<ScannedDocument>> actualScannedDocuments,
        List<InputScannedDoc> exceptionRecordScannedDocuments
    ) {
        // Scanned documents in the updated case should contain scanned documents
        // from the existing case and scanned documents from the exception record.
        // Because there is 1 scanned document in the existing case
        // and 2 scanned documents in the exception record
        // the scanned document at index 1 in the updated case should match
        // the scanned document at index 0 in the exception record and
        // the scanned document at index 2 in the updated case should match
        // the scanned document at index 1 in the exception record
        assertExceptionRecordScannedDocument(
            actualScannedDocuments.get(1).value,
            exceptionRecordScannedDocuments.get(0)
        );
        assertExceptionRecordScannedDocument(
            actualScannedDocuments.get(2).value,
            exceptionRecordScannedDocuments.get(1)
        );
    }

    private void assertExceptionRecordScannedDocument(
        ScannedDocument actualScannedDocument,
        InputScannedDoc exceptionRecordScannedDocument
    ) {
        assertThat(actualScannedDocument)
            .isEqualToIgnoringGivenFields(exceptionRecordScannedDocument,
                "exceptionRecordReference",
                "document");
        assertThat(actualScannedDocument.exceptionRecordReference).isNull();
        assertThat(actualScannedDocument.document)
            .isEqualToComparingFieldByField(
                exceptionRecordScannedDocument.document
            );
    }
}
