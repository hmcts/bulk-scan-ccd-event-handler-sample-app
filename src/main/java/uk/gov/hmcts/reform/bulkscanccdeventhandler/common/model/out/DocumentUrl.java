package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentUrl {

    @JsonProperty("document_url")
    public final String url;

    @JsonProperty("document_hash")
    public final String hash;

    @JsonProperty("document_binary_url")
    public final String binaryUrl;

    @JsonProperty("document_filename")
    public final String filename;

    public DocumentUrl(
        String url,
        String hash,
        String binaryUrl,
        String filename
    ) {
        this.url = url;
        this.hash = hash;
        this.binaryUrl = binaryUrl;
        this.filename = filename;
    }
}
