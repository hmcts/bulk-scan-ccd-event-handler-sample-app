package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.CcdCollectionElement;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.CaseCreationDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;

import java.util.List;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_2;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_3;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LEGACY_ID;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_TOWN;

@Service
public class ExceptionRecordToCaseTransformer {

    public static final String EVENT_ID = "createCase";
    public static final String CASE_TYPE_ID = "Bulk_Scanned";

    public SuccessfulTransformationResponse toCase(ExceptionRecord er) {
        SampleCase caseData = buildCase(er);

        return new SuccessfulTransformationResponse(
            new CaseCreationDetails(CASE_TYPE_ID, EVENT_ID, caseData),
            emptyList() // TODO: build warnings
        );
    }

    private SampleCase buildCase(ExceptionRecord er) {

        assertIsValid(er);

        return new SampleCase(
            getFromOcr(er, LEGACY_ID),
            getFromOcr(er, FIRST_NAME),
            getFromOcr(er, LAST_NAME),
            getFromOcr(er, DATE_OF_BIRTH),
            getFromOcr(er, CONTACT_NUMBER),
            getFromOcr(er, EMAIL),
            new Address(
                getFromOcr(er, ADDRESS_LINE_1),
                getFromOcr(er, ADDRESS_LINE_2),
                getFromOcr(er, ADDRESS_LINE_3),
                getFromOcr(er, POST_CODE),
                getFromOcr(er, POST_TOWN),
                getFromOcr(er, COUNTY),
                getFromOcr(er, COUNTRY)
            ),
            er.scannedDocuments
                .stream()
                .map(it -> new ScannedDocument(
                    it.type,
                    it.subtype,
                    it.url,
                    it.controlNumber,
                    it.fileName,
                    it.scannedDate,
                    it.deliveryDate,
                    er.id
                ))
                .map(CcdCollectionElement::new)
                .collect(toList())
        );
    }

    private void assertIsValid(ExceptionRecord exceptionRecord) {

        List<String> ocrFieldsInExceptionRecord =
            exceptionRecord
                .ocrDataFields
                .stream()
                .map(it -> it.name)
                .collect(toList());

        List<String> missingFields =
            OcrFieldNames
                .getRequiredFields()
                .stream()
                .filter(reqField -> !ocrFieldsInExceptionRecord.contains(reqField))
                .collect(toList());


        if (!missingFields.isEmpty()) {
            throw new InvalidaExceptionRecordException(
                "Missing required fields: " + String.join(", ", missingFields)
            );
        }
    }

    private String getFromOcr(ExceptionRecord er, String name) {
        return er.ocrDataFields
            .stream()
            .filter(it -> it.name.equals(name))
            .map(it -> it.value)
            .findFirst()
            .orElse(null);
    }
}
