package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.CaseCreationDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;

import static java.util.stream.Collectors.toList;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LEGACY_ID;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.OcrFieldExtractor.get;

@Service
public class TransformationInputToCaseTransformer {

    public static final String EVENT_ID = "createCase";
    public static final String CASE_TYPE_ID = "Bulk_Scanned";

    private final DocumentMapper documentMapper;
    private final AddressExtractor addressExtractor;
    private final TransformationInputValidator transformationInputValidator;
    private final CaseValidator caseValidator;

    // region constructor
    public TransformationInputToCaseTransformer(
        DocumentMapper documentMapper,
        AddressExtractor addressExtractor,
        TransformationInputValidator transformationInputValidator,
        CaseValidator caseValidator
    ) {
        this.documentMapper = documentMapper;
        this.addressExtractor = addressExtractor;
        this.transformationInputValidator = transformationInputValidator;
        this.caseValidator = caseValidator;
    }
    // endregion

    public SuccessfulTransformationResponse toCase(TransformationInput transformationInput) {
        transformationInputValidator.assertIsValid(transformationInput);

        SampleCase caseData = buildCase(transformationInput);

        return new SuccessfulTransformationResponse(
            new CaseCreationDetails(
                CASE_TYPE_ID,
                EVENT_ID,
                caseData
            ),
            caseValidator.getWarnings(caseData)
        );
    }

    private SampleCase buildCase(TransformationInput er) {
        // New transformation request contains exceptionRecordId
        // Old transformation request contains id field, which is the exception record id
        String exceptionRecordReference = StringUtils.isNotEmpty(er.exceptionRecordId) ? er.exceptionRecordId : er.id;
        return new SampleCase(
            get(er, LEGACY_ID),
            get(er, FIRST_NAME),
            get(er, LAST_NAME),
            get(er, DATE_OF_BIRTH),
            get(er, CONTACT_NUMBER),
            get(er, EMAIL),
            addressExtractor.extractFrom(er.ocrDataFields),
            er.scannedDocuments
                .stream()
                .map(it -> documentMapper.toCaseDoc(it, exceptionRecordReference))
                .collect(toList()),
            er.id
        );
    }
}
