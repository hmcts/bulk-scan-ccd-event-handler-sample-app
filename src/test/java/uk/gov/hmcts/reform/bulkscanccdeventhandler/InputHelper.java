package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDocUrl;

import static java.time.LocalDateTime.now;

public final class InputHelper {

    private InputHelper() {
        // utility class construct
    }

    public static InputScannedDoc getSampleInputDocument(String suffix) {
        return new InputScannedDoc(
            "type" + suffix,
            "subtype" + suffix,
            new InputScannedDocUrl(
                "url" + suffix,
                "binary-url" + suffix,
                "hello.pdf" + suffix
            ),
            "dcn" + suffix,
            "filename" + suffix,
            now(),
            now()
        );
    }

    public static InputScannedDoc getSampleInputDocument() {
        return getSampleInputDocument("");
    }
}
