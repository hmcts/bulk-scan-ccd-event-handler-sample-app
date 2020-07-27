package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.caseUpdateDetails;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.inputScannedDocuments;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.sampleCase;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.scannedDocuments;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.transformationInput;

@SuppressWarnings("checkstyle:lineLength")
@ExtendWith(MockitoExtension.class)
public class CaseUpdaterTest {

    @Mock private AddressExtractor addressExtractor;

    private CaseUpdater caseUpdater;

    @BeforeEach
    public void setUp() {
        this.caseUpdater = new CaseUpdater(addressExtractor);
    }

    /**
     * should override the address with exception record address data.
     * should return total document list by merging the document list with exception and case
     *
     */
    @Test
    public void should_update_case_data_with_exception_record_if_no_warnings() {
        // given
        Address exceptionRecordAddress = new Address("0", "1", "2", "3", "4", "5", "6");

        LocalDateTime caseScannedDate = now();
        LocalDateTime caseDeliveryDate = now();

        List<Item<ScannedDocument>> caseScannedDocuments = scannedDocuments(
            caseScannedDate,
            caseDeliveryDate
        );
        SampleCase originalCase = sampleCase(caseScannedDocuments);

        LocalDateTime exceptionRecordScannedDate = now();
        LocalDateTime exceptionRecordDeliveryDate = now();
        List<InputScannedDoc> exceptionRecordScannedDocuments =
            inputScannedDocuments(exceptionRecordScannedDate, exceptionRecordDeliveryDate);
        TransformationInput transformationInput = transformationInput(exceptionRecordScannedDocuments);

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            exceptionRecordScannedDocuments,
            emptyList()
        );

        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);

        // when
        SuccessfulUpdateResponse result =
            caseUpdater.update(
                new CaseUpdateRequest(
                    false,
                    transformationInput,
                    caseUpdateDetails,
                    new CaseDetails("1234567890", "some_type", originalCase)
                )
            );

        // then
        assertThat(result.caseUpdateDetails.caseData.address)
            .isEqualToComparingFieldByField(exceptionRecordAddress);
        assertCaseData(result.caseUpdateDetails.caseData, originalCase);
        assertThat(result.warnings).isEmpty();
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocuments(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDocuments,
            exceptionRecordScannedDocuments
        );
    }

    @Test
    public void should_not_update_case_data_without_any_documents() {
        // given
        SampleCase originalCase = sampleCase(emptyList());

        TransformationInput transformationInput = transformationInput(emptyList());

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(emptyList(), emptyList());

        // when
        IllegalArgumentException exception = catchThrowableOfType(() ->
                caseUpdater.update(
                    new CaseUpdateRequest(
                        false,
                        transformationInput,
                        caseUpdateDetails,
                        new CaseDetails("1234567890","some_type", originalCase)
                    )
                ),
            IllegalArgumentException.class
        );

        // then
        assertThat(exception).hasMessage("Missing scanned documents in exception record");
        verifyNoInteractions(addressExtractor);
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

    private void assertScannedDocuments(
        List<Item<ScannedDocument>> actualScannedDocuments,
        List<Item<ScannedDocument>> caseScannedDocuments,
        List<InputScannedDoc> exceptionRecordScannedDocuments
    ) {
        assertThat(actualScannedDocuments.get(0).value)
            .isEqualToComparingOnlyGivenFields(caseScannedDocuments.get(0).value,
                "type",
                "scannedDate",
                "deliveryDate",
                "fileName",
                "exceptionRecordReference",
                "controlNumber"
            );
        assertThat(actualScannedDocuments.get(0).value.document)
            .isEqualToComparingFieldByField(
                caseScannedDocuments.get(0).value.document
            );

        assertExceptionRecordScannedDocument(
            actualScannedDocuments.get(1).value,
            exceptionRecordScannedDocuments.get(0)
        );
        assertExceptionRecordScannedDocument(
            actualScannedDocuments.get(2).value,
            exceptionRecordScannedDocuments.get(1)
        );
    }

    private void assertExceptionRecordScannedDocument(ScannedDocument actualScannedDocument, InputScannedDoc exceptionRecordScannedDocument) {
        assertThat(actualScannedDocument)
            .isEqualToComparingOnlyGivenFields(exceptionRecordScannedDocument,
                "type",
                "scannedDate",
                "deliveryDate",
                "fileName",
                "controlNumber"
            );
        assertThat(actualScannedDocument.exceptionRecordReference).isNull();
        assertThat(actualScannedDocument.document)
            .isEqualToComparingFieldByField(
                exceptionRecordScannedDocument.document
            );
    }
}
