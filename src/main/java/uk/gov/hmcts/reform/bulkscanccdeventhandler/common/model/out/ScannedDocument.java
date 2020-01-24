package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ScannedDocument {

    @JsonProperty("type")
    public final String type;

    @JsonProperty("subtype")
    public final String subtype;

    @JsonProperty("url")
    public final DocumentUrl document;

    @JsonProperty("control_number")
    public final String controlNumber;

    @JsonProperty("file_name")
    public final String fileName;

    @JsonProperty("scanned_date")
    public final LocalDateTime scannedDate;

    @JsonProperty("delivery_date")
    public final LocalDateTime deliveryDate;

    @JsonProperty("exception_record_reference")
    public final String exceptionRecordReference;

    public ScannedDocument(
        @JsonProperty("type") String type,
        @JsonProperty("subtype") String subtype,
        @JsonProperty("url") DocumentUrl document,
        @JsonProperty("control_number") String controlNumber,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("scanned_date") LocalDateTime scannedDate,
        @JsonProperty("delivery_date") LocalDateTime deliveryDate,
        @JsonProperty("exception_record_reference") String exceptionRecordReference
    ) {
        this.type = type;
        this.subtype = subtype;
        this.document = document;
        this.controlNumber = controlNumber;
        this.fileName = fileName;
        this.scannedDate = scannedDate;
        this.deliveryDate = deliveryDate;
        this.exceptionRecordReference = exceptionRecordReference;
    }
}
