package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.time.LocalDateTime;
import java.util.List;

public class TransformationInput {

    public final String id;
    public final String caseTypeId;
    public final String poBox;
    public final String jurisdiction;
    public final String formType;
    public final JourneyClassification journeyClassification;
    public final LocalDateTime deliveryDate;
    public final LocalDateTime openingDate;
    public final List<InputScannedDoc> scannedDocuments;
    public final List<OcrDataField> ocrDataFields;
    public final String envelopeId;
    public final boolean isAutomatedProcess;
    public final String exceptionRecordId;
    public final String exceptionRecordCaseTypeId;

    /**
     * Used for 2 transformation endpoints:
     * <ul>
     *     <li>Exception record transformation</li>
     *     <li>Auto case creation transformation</li>
     * </ul>
     * The following fields are added for Auto Case creation transformation request.
     * <ul>
     *     <li>envelope_id</li>
     *     <li>is_automated_process</li>
     *     <li>exception_record_id - replaces id in the Exception Record transformation request</li>
     *     <li>exception_record_case_type_id - replaces case_type_id in the Exception Record transformation</li>
     * </ul>
     * id and case_type_id fields can be removed after moving to the Auto case creation transformation endpoint.
     */
    public TransformationInput(
        @JsonProperty("id") String id,
        @JsonProperty("case_type_id") String caseTypeId,
        @JsonProperty("po_box") String poBox,
        @JsonProperty("po_box_jurisdiction") String jurisdiction,
        @JsonProperty("form_type") String formType,
        @JsonProperty("journey_classification") JourneyClassification journeyClassification,
        @JsonProperty("delivery_date") LocalDateTime deliveryDate,
        @JsonProperty("opening_date") LocalDateTime openingDate,
        @JsonProperty("scanned_documents") List<InputScannedDoc> scannedDocuments,
        @JsonProperty("ocr_data_fields") List<OcrDataField> ocrDataFields,
        // Auto Case creation request fields
        @JsonProperty("envelope_id") String envelopeId,
        @JsonProperty("is_automated_process") boolean isAutomatedProcess,
        @JsonProperty("exception_record_id") String exceptionRecordId,
        @JsonProperty("exception_record_case_type_id") String exceptionRecordCaseTypeId
    ) {
        this.id = id;
        this.caseTypeId = caseTypeId;
        this.poBox = poBox;
        this.jurisdiction = jurisdiction;
        this.formType = formType;
        this.journeyClassification = journeyClassification;
        this.deliveryDate = deliveryDate;
        this.openingDate = openingDate;
        this.scannedDocuments = scannedDocuments;
        this.ocrDataFields = ocrDataFields;
        this.envelopeId = envelopeId;
        this.isAutomatedProcess = isAutomatedProcess;
        this.exceptionRecordId = exceptionRecordId;
        this.exceptionRecordCaseTypeId = exceptionRecordCaseTypeId;
    }
}
