package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
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
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowableOfType;
import static org.assertj.core.api.Assertions.tuple;
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
    @Mock private CaseUpdateRequestValidator caseUpdateRequestValidator;
    @Mock private UpdatedCaseValidator updatedCaseValidator;

    private CaseUpdater caseUpdater;

    @BeforeEach
    public void setUp() {
        this.caseUpdater = new CaseUpdater(
            caseUpdateRequestValidator,
            updatedCaseValidator,
            addressExtractor
        );
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

        List<Item<ScannedDocument>> scannedDocuments = scannedDocuments(
            caseScannedDate,
            caseDeliveryDate
        );
        SampleCase originalCase = sampleCase(scannedDocuments);

        LocalDateTime exScannedDate = now();
        LocalDateTime exDeliveryDate = now();
        List<InputScannedDoc> inputScannedDocuments =
            inputScannedDocuments(exScannedDate, exDeliveryDate);
        TransformationInput transformationInput = transformationInput(inputScannedDocuments);

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocuments,
            emptyList()
        );

        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);
        given(updatedCaseValidator.getWarnings(any(SampleCase.class))).willReturn(emptyList());

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
        assertAddress(result.caseUpdateDetails.caseData.address, exceptionRecordAddress);
        assertCaseData(result.caseUpdateDetails.caseData, originalCase);
        assertThat(result.warnings).isEmpty();
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocuments(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDate,
            caseDeliveryDate,
            exScannedDate,
            exDeliveryDate
        );
    }

    @Test
    public void should_update_case_data_with_exception_record_if_auto_case_update_request_is_false_and_warnings() {
        // given
        Address exceptionRecordAddress = new Address("0", "1", "2", "3", "4", "5", "6");

        LocalDateTime caseScannedDate = now();
        LocalDateTime caseDeliveryDate = now();

        List<Item<ScannedDocument>> scannedDocuments = scannedDocuments(
            caseScannedDate,
            caseDeliveryDate
        );
        SampleCase originalCase = sampleCase(scannedDocuments);

        LocalDateTime exScannedDate = now();
        LocalDateTime exDeliveryDate = now();
        List<InputScannedDoc> inputScannedDocuments =
            inputScannedDocuments(exScannedDate, exDeliveryDate);
        TransformationInput transformationInput = transformationInput(inputScannedDocuments);

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocuments,
            emptyList()
        );

        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);
        given(updatedCaseValidator.getWarnings(any(SampleCase.class))).willReturn(asList("w1", "w2"));

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
        assertAddress(result.caseUpdateDetails.caseData.address, exceptionRecordAddress);
        assertCaseData(result.caseUpdateDetails.caseData, originalCase);
        assertThat(result.warnings).containsExactlyInAnyOrder("w1", "w2");
        assertThat(result.caseUpdateDetails.eventId).isEqualTo(CaseUpdater.EVENT_ID);
        assertScannedDocuments(
            result.caseUpdateDetails.caseData.scannedDocuments,
            caseScannedDate,
            caseDeliveryDate,
            exScannedDate,
            exDeliveryDate
        );
    }

    @Test
    public void should_throw_422_for_auto_case_update_request_when_validation_warnings() {
        // given
        Address exceptionRecordAddress = new Address("0", "1", "2", "3", "4", "5", "6");

        LocalDateTime caseScannedDate = now();
        LocalDateTime caseDeliveryDate = now();

        List<Item<ScannedDocument>> scannedDocuments = scannedDocuments(
            caseScannedDate,
            caseDeliveryDate
        );
        SampleCase originalCase = sampleCase(scannedDocuments);

        LocalDateTime exScannedDate = now();
        LocalDateTime exDeliveryDate = now();
        List<InputScannedDoc> inputScannedDocuments =
            inputScannedDocuments(exScannedDate, exDeliveryDate);
        TransformationInput transformationInput = transformationInput(inputScannedDocuments);

        CaseUpdateDetails caseUpdateDetails = caseUpdateDetails(
            inputScannedDocuments,
            emptyList()
        );

        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);
        given(updatedCaseValidator.getWarnings(any(SampleCase.class))).willReturn(asList("w1", "w2"));

        // when
        HttpClientErrorException.UnprocessableEntity exc = catchThrowableOfType(
            () -> caseUpdater.update(
                new CaseUpdateRequest(
                    true,
                    transformationInput,
                    caseUpdateDetails,
                    new CaseDetails("1234567890", "some_type", originalCase)
                )
            ),
            HttpClientErrorException.UnprocessableEntity.class
        );

        // then
        assertThat(exc.getResponseBodyAsString()).isEqualTo("w1,w2");
    }

    private void assertCaseData(SampleCase resultCaseData, SampleCase originalCase) {
        assertThat(resultCaseData)
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

    private void assertAddress(Address resultCaseAddress, Address exceptionRecordAddress) {
        assertThat(resultCaseAddress.addressLine1).isEqualTo(exceptionRecordAddress.addressLine1);
        assertThat(resultCaseAddress.addressLine2).isEqualTo(exceptionRecordAddress.addressLine2);
        assertThat(resultCaseAddress.addressLine3).isEqualTo(exceptionRecordAddress.addressLine3);
        assertThat(resultCaseAddress.country).isEqualTo(exceptionRecordAddress.country);
        assertThat(resultCaseAddress.county).isEqualTo(exceptionRecordAddress.county);
        assertThat(resultCaseAddress.postCode).isEqualTo(exceptionRecordAddress.postCode);
        assertThat(resultCaseAddress.postTown).isEqualTo(exceptionRecordAddress.postTown);
    }

    private void assertScannedDocuments(
        List<Item<ScannedDocument>> resultScannedDocuments,
        LocalDateTime caseScannedDate,
        LocalDateTime caseDeliveryDate,
        LocalDateTime exScannedDate,
        LocalDateTime exDeliveryDate
    ) {
        assertThat(resultScannedDocuments.get(0).value.type).isEqualTo("type_A");
        assertThat(resultScannedDocuments.get(0).value.subtype).isEqualTo("subtype_A");
        assertThat(resultScannedDocuments.get(0).value.scannedDate).isEqualTo(caseScannedDate);
        assertThat(resultScannedDocuments.get(0).value.deliveryDate).isEqualTo(caseDeliveryDate);
        assertThat(resultScannedDocuments.get(0).value.fileName).isEqualTo("file_name_AA");
        assertThat(resultScannedDocuments.get(0).value.exceptionRecordReference).isEqualTo("exceptionRecordReference");
        assertThat(resultScannedDocuments.get(0).value.controlNumber).isEqualTo("control_number_A");
        assertThat(resultScannedDocuments.get(0).value.document.url).isEqualTo("file://file_A");
        assertThat(resultScannedDocuments.get(0).value.document.filename).isEqualTo("file_name_A");
        assertThat(resultScannedDocuments.get(0).value.document.binaryUrl).isEqualTo("binary_url_A");

        assertThat(resultScannedDocuments.get(1).value.type).isEqualTo("Form_1");
        assertThat(resultScannedDocuments.get(1).value.subtype).isEqualTo("subtype_1");
        assertThat(resultScannedDocuments.get(1).value.scannedDate).isEqualTo(exScannedDate);
        assertThat(resultScannedDocuments.get(1).value.deliveryDate).isEqualTo(exDeliveryDate);
        assertThat(resultScannedDocuments.get(1).value.fileName).isEqualTo("file_name_11");
        assertThat(resultScannedDocuments.get(1).value.exceptionRecordReference).isNull();
        assertThat(resultScannedDocuments.get(1).value.controlNumber).isEqualTo("control_number_1");
        assertThat(resultScannedDocuments.get(1).value.document.url).isEqualTo("file://file_1");
        assertThat(resultScannedDocuments.get(1).value.document.filename).isEqualTo("file_name_1");
        assertThat(resultScannedDocuments.get(1).value.document.binaryUrl).isEqualTo("binary_url_1");


        assertThat(resultScannedDocuments.get(2).value.type).isEqualTo("Form_2");
        assertThat(resultScannedDocuments.get(2).value.subtype).isEqualTo("subtype_2");
        assertThat(resultScannedDocuments.get(2).value.scannedDate).isEqualTo(exScannedDate);
        assertThat(resultScannedDocuments.get(2).value.deliveryDate).isEqualTo(exDeliveryDate);
        assertThat(resultScannedDocuments.get(2).value.fileName).isEqualTo("file_name_22");
        assertThat(resultScannedDocuments.get(2).value.exceptionRecordReference).isNull();
        assertThat(resultScannedDocuments.get(2).value.controlNumber).isEqualTo("control_number_2");
        assertThat(resultScannedDocuments.get(2).value.document.url).isEqualTo("file://file_2");
        assertThat(resultScannedDocuments.get(2).value.document.filename).isEqualTo("file_name_2");
        assertThat(resultScannedDocuments.get(2).value.document.binaryUrl).isEqualTo("binary_url_2");
    }
}
