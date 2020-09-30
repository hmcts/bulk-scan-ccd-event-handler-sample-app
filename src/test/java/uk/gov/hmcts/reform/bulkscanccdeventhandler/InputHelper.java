package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDocUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.DocumentUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.List;

import static java.time.LocalDateTime.now;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

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

    public static CaseDetails caseDetails(SampleCase originalCase) {
        return new CaseDetails("1234567890", "some_type", originalCase);
    }

    public static SampleCase sampleCase(List<Item<ScannedDocument>> scannedDocuments) {
        return new SampleCase(
            "legacy-id",
            "first-name",
            "last-name",
            "date-of-birth",
            "contact-number",
            "email",
            address(""),
            scannedDocuments,
            "er-id"
        );
    }

    public static Address address(String suffix) {
        return new Address(
            "addr-line-1" + suffix,
            "addr-line-2" + suffix,
            "addr-line-3" + suffix,
            "post-code" + suffix,
            "post-town" + suffix,
            "county" + suffix,
            "country" + suffix
        );
    }

    public static List<Item<ScannedDocument>> scannedDocuments() {
        return singletonList(
            new Item<>(
                new ScannedDocument(
                    "type_A",
                    "subtype_A",
                    new DocumentUrl("file://file_A", "binary_url_A", "file_name_A"),
                    "control_number_A",
                    "file_name_AA",
                    now(),
                    now(),
                    "exceptionRecordReference"
                )
            )
        );
    }

    public static List<InputScannedDoc> inputScannedDocuments() {
        return asList(
            new InputScannedDoc(
                "Form_1",
                "subtype_1",
                new InputScannedDocUrl("file://file_1", "binary_url_1", "file_name_1"),
                "control_number_1",
                "file_name_11",
                now(),
                now()
            ),
            new InputScannedDoc(
                "Form_2",
                "subtype_2",
                new InputScannedDocUrl("file://file_2", "binary_url_2", "file_name_2"),
                "control_number_2",
                "file_name_22",
                now(),
                now()
            )
        );
    }

    public static CaseUpdateDetails caseUpdateDetails(
        List<InputScannedDoc> scannedDocuments,
        List<OcrDataField> ocrData
    ) {
        return new CaseUpdateDetails(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            "er-form-type",
            JourneyClassification.SUPPLEMENTARY_EVIDENCE_WITH_OCR,
            now(),
            now(),
            scannedDocuments,
            ocrData,
            null
        );
    }
}
