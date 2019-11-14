package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public class InputScannedDoc {

    public final String type;
    public final String subtype;
    public final InputScannedDocUrl document;
    public final String controlNumber;
    public final String fileName;
    public final LocalDateTime scannedDate;
    public final LocalDateTime deliveryDate;

    public InputScannedDoc(
        @JsonProperty("type") String type,
        @JsonProperty("subtype") String subtype,
        @JsonProperty("url") InputScannedDocUrl document,
        @JsonProperty("control_number") String controlNumber,
        @JsonProperty("file_name") String fileName,
        @JsonProperty("scanned_date") LocalDateTime scannedDate,
        @JsonProperty("delivery_date") LocalDateTime deliveryDate
    ) {
        this.type = type;
        this.subtype = subtype;
        this.document = document;
        this.controlNumber = controlNumber;
        this.fileName = fileName;
        this.scannedDate = scannedDate;
        this.deliveryDate = deliveryDate;
    }
}
