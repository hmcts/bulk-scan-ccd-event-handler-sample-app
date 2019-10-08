package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InputScannedDocumentUrl {

    @JsonProperty("document_url")
    public final String documentUrl;

    @JsonProperty("document_filename")
    public final String documentFilename;

    @JsonProperty("document_binary_url")
    public final String documentBinaryUrl;

    public InputScannedDocumentUrl(
        String documentUrl,
        String documentFilename,
        String documentBinaryUrl
    ) {
        this.documentUrl = documentUrl;
        this.documentFilename = documentFilename;
        this.documentBinaryUrl = documentBinaryUrl;
    }
}
