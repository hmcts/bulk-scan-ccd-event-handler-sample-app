package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.DocumentUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;

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
            return new Item<>(new ScannedDocument(
                exceptionRecordDoc.type,
                exceptionRecordDoc.subtype,
                new DocumentUrl(
                    exceptionRecordDoc.document.url,
                    exceptionRecordDoc.document.hash,
                    exceptionRecordDoc.document.binaryUrl,
                    exceptionRecordDoc.document.filename
                ),
                exceptionRecordDoc.controlNumber,
                exceptionRecordDoc.fileName,
                exceptionRecordDoc.scannedDate,
                exceptionRecordDoc.deliveryDate,
                exceptionRecordReference
            ));
        }
    }
}
