package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;

import java.util.Collections;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

public class ExceptionRecordToCaseTransformerTest {

    private ExceptionRecordToCaseTransformer service = new ExceptionRecordToCaseTransformer();

    @Test
    public void should_throw_exception_if_exception_record_is_missing_required_ocr_fields() {
        // given
        ExceptionRecord exceptionRecord = new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            JourneyClassification.NEW_APPLICATION,
            now(),
            now(),
            Collections.emptyList(),
            asList(
                new OcrDataField(OcrFieldNames.CONTACT_NUMBER, "555-555-555"),
                new OcrDataField(OcrFieldNames.EMAIL, "test@example.com")
            )
        );

        // when
        Throwable exc = catchThrowable(() -> service.toCase(exceptionRecord));

        // then
        assertThat(exc)
            .isInstanceOf(InvalidaExceptionRecordException.class)
            .hasMessageContaining("Missing required fields")
            .hasMessageContaining(OcrFieldNames.FIRST_NAME)
            .hasMessageContaining(OcrFieldNames.LAST_NAME);
    }

    @Test
    public void should_map_exception_record_to_a_case() {
        // given
        ScannedDocument erDoc1 = new ScannedDocument(
            "type1",
            "subtype1",
            "url1",
            "dcn1",
            "filename1",
            now(),
            now()
        );

        ScannedDocument erDoc2 = new ScannedDocument(
            "type2",
            "subtype2",
            "url2",
            "dcn2",
            "filename2",
            now(),
            now()
        );


        ExceptionRecord exceptionRecord = new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            JourneyClassification.NEW_APPLICATION,
            now(),
            now(),
            asList(
                erDoc1,
                erDoc2
            ),
            asList(
                new OcrDataField(OcrFieldNames.FIRST_NAME, "John"),
                new OcrDataField(OcrFieldNames.LAST_NAME, "Smith"),
                new OcrDataField(OcrFieldNames.ADDRESS_LINE_1, "address_line_1"),
                new OcrDataField(OcrFieldNames.ADDRESS_LINE_2, "address_line_2"),
                new OcrDataField(OcrFieldNames.ADDRESS_LINE_3, "address_line_3"),
                new OcrDataField(OcrFieldNames.COUNTY, "county")
            )
        );

        // when
        SuccessfulTransformationResponse result = service.toCase(exceptionRecord);
        SampleCase caseData = result.caseCreationDetails.caseData;

        // then
        assertThat(result.warnings).isEmpty();
        assertThat(result.caseCreationDetails.caseTypeId).isEqualTo(ExceptionRecordToCaseTransformer.CASE_TYPE_ID);
        assertThat(result.caseCreationDetails.eventId).isEqualTo(ExceptionRecordToCaseTransformer.EVENT_ID);

        assertThat(caseData.firstName).isEqualTo("John");
        assertThat(caseData.lastName).isEqualTo("Smith");
        assertThat(caseData.address.addressLine1).isEqualTo("address_line_1");
        assertThat(caseData.address.addressLine2).isEqualTo("address_line_2");
        assertThat(caseData.address.addressLine3).isEqualTo("address_line_3");
        assertThat(caseData.address.county).isEqualTo("county");

        assertThat(caseData.scannedDocuments).hasSize(2);
        assertThat(caseData.scannedDocuments)
            .as("Documents should reference source exception record")
            .extracting(doc -> doc.value.exceptionRecordReference)
            .containsExactly(exceptionRecord.id, exceptionRecord.id);

        checkScannedDoc(caseData.scannedDocuments.get(0).value, erDoc1);
        checkScannedDoc(caseData.scannedDocuments.get(1).value, erDoc2);
    }

    private void checkScannedDoc(
        uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument caseDoc, // TODO: rename type
        ScannedDocument erDoc
    ) {
        assertThat(caseDoc.controlNumber).isEqualTo(erDoc.controlNumber);
        assertThat(caseDoc.fileName).isEqualTo(erDoc.fileName);
        assertThat(caseDoc.type).isEqualTo(erDoc.type);
        assertThat(caseDoc.subtype).isEqualTo(erDoc.subtype);
        assertThat(caseDoc.url).isEqualTo(erDoc.url);
        assertThat(caseDoc.deliveryDate).isEqualTo(erDoc.deliveryDate);
        assertThat(caseDoc.scannedDate).isEqualTo(erDoc.scannedDate);
    }
}
