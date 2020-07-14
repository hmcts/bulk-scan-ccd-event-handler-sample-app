package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.time.LocalDateTime;
import java.util.List;

public class CaseUpdateDetailsRequest {
    public final String exceptionRecordId;
    public final String exceptionRecordCaseTypeId;
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

    public CaseUpdateDetailsRequest(
        @JsonProperty("exception_record_id") String exceptionRecordId,
        @JsonProperty("exception_record_case_type_id") String exceptionRecordCaseTypeId,
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
        @JsonProperty("is_automated_process") boolean isAutomatedProcess
    ) {
        this.exceptionRecordId = exceptionRecordId;
        this.exceptionRecordCaseTypeId = exceptionRecordCaseTypeId;
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
    }
}
