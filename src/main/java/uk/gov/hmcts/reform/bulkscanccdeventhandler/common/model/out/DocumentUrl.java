package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DocumentUrl {

    @JsonProperty("document_url")
    public final String url;

    @JsonProperty("document_binary_url")
    public final String binaryUrl;

    @JsonProperty("document_filename")
    public final String filename;

    public DocumentUrl(
        @JsonProperty("document_url") String url,
        @JsonProperty("document_binary_url") String binaryUrl,
        @JsonProperty("document_filename") String filename
    ) {
        this.url = url;
        this.binaryUrl = binaryUrl;
        this.filename = filename;
    }
}
