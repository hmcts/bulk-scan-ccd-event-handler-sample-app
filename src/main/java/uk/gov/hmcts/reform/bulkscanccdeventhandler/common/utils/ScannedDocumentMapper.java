package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.DocumentUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;

public class ScannedDocumentMapper {

    private ScannedDocumentMapper() {
    }

    public static ScannedDocument mapToScannedDocument(InputScannedDoc inputScannedDoc) {

        DocumentUrl documentUrl = new DocumentUrl(
            inputScannedDoc.document.url,
            inputScannedDoc.document.hash,
            inputScannedDoc.document.binaryUrl,
            inputScannedDoc.document.filename
        );

        return new ScannedDocument(
            inputScannedDoc.type,
            inputScannedDoc.subtype,
            documentUrl,
            inputScannedDoc.controlNumber,
            inputScannedDoc.fileName,
            inputScannedDoc.scannedDate,
            inputScannedDoc.deliveryDate,
            null
        );

    }
}
