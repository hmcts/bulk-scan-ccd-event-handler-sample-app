package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.InputHelper.getSampleInputDocument;

public class DocumentMapperTest {

    private final DocumentMapper mapper = new DocumentMapper();

    @Test
    public void should_map_models() {
        // given
        InputScannedDoc input = getSampleInputDocument();
        String refId = "ref-id";

        // when
        Item<ScannedDocument> output = mapper.toCaseDoc(input, refId, false);

        // then
        assertCaseDocuments(input, output, refId);
    }

    @Test
    public void should_map_documents_for_auto_case_creation_when_exception_record_id_is_null() {
        // given
        InputScannedDoc input = getSampleInputDocument();
        String refId = "ref-id";

        // when
        Item<ScannedDocument> output = mapper.toCaseDoc(input, null, true);

        // then
        assertCaseDocuments(input, output, null);
    }

    @Test
    public void should_map_null_to_null() {
        // given
        InputScannedDoc input = null;
        String refId = "ref-id";

        // when
        Item<ScannedDocument> output = mapper.toCaseDoc(input, refId, false);

        // then
        assertThat(output).isNull();
    }

    private void assertCaseDocuments(InputScannedDoc input, Item<ScannedDocument> output, String exceptionRecordId) {
        assertSoftly(softly -> {
            softly.assertThat(output.value.type).isEqualTo(input.type);
            softly.assertThat(output.value.subtype).isEqualTo(input.subtype);
            softly.assertThat(output.value.document)
                .usingRecursiveComparison()
                .isEqualTo(input.document);
            softly.assertThat(output.value.controlNumber).isEqualTo(input.controlNumber);
            softly.assertThat(output.value.fileName).isEqualTo(input.fileName);
            softly.assertThat(output.value.deliveryDate).isEqualTo(input.deliveryDate);
            softly.assertThat(output.value.scannedDate).isEqualTo(input.scannedDate);
            softly.assertThat(output.value.exceptionRecordReference).isEqualTo(exceptionRecordId);
        });
    }
}
