package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ScannedDocumentUrl;

@Component
public class DocumentMapper {

    /**
     * Converts document in Exception Record model to document in Case model.
     */
    public Item<ScannedDocument> toCaseDoc(
        InputScannedDoc exceptionRecordDoc,
        String exceptionRecordReference
    ) {
        if (exceptionRecordDoc == null) {
            return null;
        } else {
            ScannedDocumentUrl url = new ScannedDocumentUrl(
                exceptionRecordDoc.url.documentUrl,
                exceptionRecordDoc.url.documentFilename,
                exceptionRecordDoc.url.documentBinaryUrl
            );
            return new Item<>(new ScannedDocument(
                exceptionRecordDoc.type,
                exceptionRecordDoc.subtype,
                url,
                exceptionRecordDoc.controlNumber,
                exceptionRecordDoc.fileName,
                exceptionRecordDoc.scannedDate,
                exceptionRecordDoc.deliveryDate,
                exceptionRecordReference
            ));
        }
    }
}
