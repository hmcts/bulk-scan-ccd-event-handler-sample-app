package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdate;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDocUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.DocumentUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

import java.time.LocalDateTime;
import java.util.Arrays;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verifyNoInteractions;

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
    public void should_update_case_data_with_exception_record() {
        // given

        Address originalCaseAddress = new Address("a", "b", "c", "d", "e", "f", "g");
        Address exceptionRecordAddress = new Address("0", "1", "2", "3", "4", "5", "6");

        LocalDateTime caseScannedDate = now();
        LocalDateTime caseDeliveryDate = now();

        SampleCase originalCase = new SampleCase(
            "legacy-id",
            "first-name",
            "last-name",
            "date-of-birth",
            "contact-number",
            "email",
            originalCaseAddress,
            Arrays.asList(
                new Item<ScannedDocument>(
                    new ScannedDocument(
                        "type_A",
                        "subtype_A",
                        new DocumentUrl("file://file_A", "binary_url_A", "file_name_A"),
                        "control_number_A",
                        "file_name_AA",
                        caseScannedDate,
                        caseDeliveryDate,
                        "exceptionRecordReference"
                    )
                )
            ),
            "er-id"
        );

        LocalDateTime exScannedDate = now();
        LocalDateTime exDeliveryDate = now();

        ExceptionRecord exceptionRecord = new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            "er-form-type",
            JourneyClassification.SUPPLEMENTARY_EVIDENCE_WITH_OCR,
            now(),
            now(),
            Arrays.asList(
                new InputScannedDoc(
                    "Form_1",
                    "subtype_1",
                    new InputScannedDocUrl("file://file_1", "binary_url_1", "file_name_1"),
                    "control_number_1",
                    "file_name_11",
                    exScannedDate,
                    exDeliveryDate),
                new InputScannedDoc(
                    "Form_2",
                    "subtype_2",
                    new InputScannedDocUrl("file://file_2", "binary_url_2", "file_name_2"),
                    "control_number_2",
                    "file_name_22",
                    exScannedDate,
                    exDeliveryDate)
            ),
            emptyList(),
            null,
            null,
            null,
            null
        );

        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);

        // when
        SuccessfulUpdateResponse result =
            caseUpdater.update(
                new CaseUpdate(
                    exceptionRecord,
                    new CaseDetails("1234567890", "some_type", originalCase)
                )
            );

        // then
        assertThat(result.caseUpdateDetails.caseData.address.addressLine1).isEqualTo(exceptionRecordAddress.addressLine1);
        assertThat(result.caseUpdateDetails.caseData.address.addressLine2).isEqualTo(exceptionRecordAddress.addressLine2);
        assertThat(result.caseUpdateDetails.caseData.address.addressLine3).isEqualTo(exceptionRecordAddress.addressLine3);
        assertThat(result.caseUpdateDetails.caseData.address.country).isEqualTo(exceptionRecordAddress.country);
        assertThat(result.caseUpdateDetails.caseData.address.county).isEqualTo(exceptionRecordAddress.county);
        assertThat(result.caseUpdateDetails.caseData.address.postCode).isEqualTo(exceptionRecordAddress.postCode);
        assertThat(result.caseUpdateDetails.caseData.address.postTown).isEqualTo(exceptionRecordAddress.postTown);

        assertThat(result.caseUpdateDetails.caseData)
            .extracting(c -> tuple(
                c.legacyId,
                c.firstName,
                c.lastName,
                c.dateOfBirth,
                c.bulkScanCaseReference
            ))
            .isEqualTo(tuple(
                originalCase.legacyId,
                originalCase.firstName,
                originalCase.lastName,
                originalCase.dateOfBirth,
                originalCase.bulkScanCaseReference
            ));

        assertThat(result.warnings).isEmpty();
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.type)
            .isEqualTo("type_A");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.subtype)
            .isEqualTo("subtype_A");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.scannedDate)
            .isEqualTo(caseScannedDate);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.deliveryDate)
            .isEqualTo(caseDeliveryDate);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.fileName)
            .isEqualTo("file_name_AA");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.exceptionRecordReference)
            .isEqualTo("exceptionRecordReference");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.controlNumber)
            .isEqualTo("control_number_A");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.document.url)
            .isEqualTo("file://file_A");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.document.filename)
            .isEqualTo("file_name_A");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(0).value.document.binaryUrl)
            .isEqualTo("binary_url_A");

        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.type)
            .isEqualTo("Form_1");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.subtype)
            .isEqualTo("subtype_1");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.scannedDate)
            .isEqualTo(exScannedDate);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.deliveryDate)
            .isEqualTo(exDeliveryDate);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.fileName)
            .isEqualTo("file_name_11");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.exceptionRecordReference)
            .isNull();
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.controlNumber)
            .isEqualTo("control_number_1");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.document.url)
            .isEqualTo("file://file_1");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.document.filename)
            .isEqualTo("file_name_1");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(1).value.document.binaryUrl)
            .isEqualTo("binary_url_1");


        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.type)
            .isEqualTo("Form_2");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.subtype)
            .isEqualTo("subtype_2");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.scannedDate)
            .isEqualTo(exScannedDate);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.deliveryDate)
            .isEqualTo(exDeliveryDate);
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.fileName)
            .isEqualTo("file_name_22");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.exceptionRecordReference)
            .isNull();
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.controlNumber)
            .isEqualTo("control_number_2");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.document.url)
            .isEqualTo("file://file_2");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.document.filename)
            .isEqualTo("file_name_2");
        assertThat(result.caseUpdateDetails.caseData.scannedDocuments.get(2).value.document.binaryUrl)
            .isEqualTo("binary_url_2");

    }

    @Test
    public void should_not_update_case_data_without_any_documents() {
        // given

        Address originalCaseAddress = new Address("a", "b", "c", "d", "e", "f", "g");
        Address exceptionRecordAddress = new Address("0", "1", "2", "3", "4", "5", "6");

        LocalDateTime caseScannedDate = now();
        LocalDateTime caseDeliveryDate = now();

        SampleCase originalCase = new SampleCase(
            "legacy-id",
            "first-name",
            "last-name",
            "date-of-birth",
            "contact-number",
            "email",
            originalCaseAddress,
            emptyList(),
            "er-id"
        );

        ExceptionRecord exceptionRecord = new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            "er-form-type",
            JourneyClassification.SUPPLEMENTARY_EVIDENCE_WITH_OCR,
            now(),
            now(),
            emptyList(),
            emptyList(),
            null,
            null,
            null,
            null
        );

        // when
        IllegalArgumentException exception = catchThrowableOfType(() ->
            caseUpdater.update(
                new CaseUpdate(
                    exceptionRecord,
                    new CaseDetails("1234567890","some_type", originalCase)
                )
            ),
            IllegalArgumentException.class
        );

        // then
        assertThat(exception).hasMessage("Missing scanned documents in exception record");
        verifyNoInteractions(addressExtractor);
    }
}
