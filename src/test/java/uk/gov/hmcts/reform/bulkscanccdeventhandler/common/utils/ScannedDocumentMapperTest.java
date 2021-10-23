package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDocUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.ScannedDocumentMapper.mapToScannedDocument;

public class ScannedDocumentMapperTest {

    @Test
    void should_map_inputScannedDoc_to_ScannedDocument() {
        LocalDateTime scannedDate = LocalDateTime.now();
        LocalDateTime deliveryDate = LocalDateTime.now();

        InputScannedDocUrl document = new InputScannedDocUrl(
            "file://pathtofile/file",
            null,
            "binaryurl",
            "filename"
        );

        InputScannedDoc inputScannedDoc = new InputScannedDoc(
            "Form",
            "B123",
            document,
            "doc_123456",
            "filename_123",
            scannedDate,
            deliveryDate
        );

        ScannedDocument scannedDocument = mapToScannedDocument(inputScannedDoc);

        assertThat(scannedDocument.controlNumber).isEqualTo(inputScannedDoc.controlNumber);
        assertThat(scannedDocument.deliveryDate).isEqualTo(inputScannedDoc.deliveryDate);
        assertThat(scannedDocument.exceptionRecordReference).isEqualTo(null);
        assertThat(scannedDocument.fileName).isEqualTo(inputScannedDoc.fileName);
        assertThat(scannedDocument.scannedDate).isEqualTo(inputScannedDoc.scannedDate);
        assertThat(scannedDocument.subtype).isEqualTo(inputScannedDoc.subtype);
        assertThat(scannedDocument.type).isEqualTo(inputScannedDoc.type);
        assertThat(scannedDocument.document.binaryUrl).isEqualTo(inputScannedDoc.document.binaryUrl);
        assertThat(scannedDocument.document.filename).isEqualTo(inputScannedDoc.document.filename);
        assertThat(scannedDocument.document.url).isEqualTo(inputScannedDoc.document.url);


    }
}
