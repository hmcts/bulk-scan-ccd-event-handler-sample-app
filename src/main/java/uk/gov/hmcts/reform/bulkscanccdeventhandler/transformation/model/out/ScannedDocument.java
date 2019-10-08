package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class ScannedDocument {

    @JsonProperty
    public final String type;

    @JsonProperty
    public final String subtype;

    @JsonProperty("url")
    public final DocumentUrl document;

    @JsonProperty
    public final String controlNumber;

    @JsonProperty
    public final String fileName;

    @JsonProperty
    public final LocalDateTime scannedDate;

    @JsonProperty
    public final LocalDateTime deliveryDate;

    @JsonProperty
    public final String exceptionRecordReference;

    public ScannedDocument(
        @JsonProperty("type") String type,
        @JsonProperty("subtype") String subtype,
        @JsonProperty("url") DocumentUrl document,
        @JsonProperty("controlNumber") String controlNumber,
        @JsonProperty("fileName") String fileName,
        @JsonProperty("scannedDate") LocalDateTime scannedDate,
        @JsonProperty("deliveryDate") LocalDateTime deliveryDate,
        @JsonProperty("exceptionRecordReference") String exceptionRecordReference
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
