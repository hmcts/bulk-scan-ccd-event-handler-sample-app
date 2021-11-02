package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputScannedDocUrl {

    public final String url;

    public final String documentHash;

    public final String binaryUrl;

    public final String filename;

    public InputScannedDocUrl(
        @JsonProperty("document_url") String url,
        @JsonProperty(value = "document_hash", required = false) String documentHash,
        @JsonProperty("document_binary_url") String binaryUrl,
        @JsonProperty("document_filename") String filename
    ) {
        this.url = url;
        this.documentHash = documentHash;
        this.binaryUrl = binaryUrl;
        this.filename = filename;
    }
}
