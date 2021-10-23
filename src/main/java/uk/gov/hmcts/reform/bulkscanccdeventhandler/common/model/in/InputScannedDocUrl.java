package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputScannedDocUrl {

    public final String url;

    public final String hash;

    public final String binaryUrl;

    public final String filename;

    public InputScannedDocUrl(
        @JsonProperty("document_url") String url,
        @JsonProperty("document_hash") String hash,
        @JsonProperty("document_binary_url") String binaryUrl,
        @JsonProperty("document_filename") String filename
    ) {
        this.url = url;
        this.hash = hash;
        this.binaryUrl = binaryUrl;
        this.filename = filename;
    }
}
