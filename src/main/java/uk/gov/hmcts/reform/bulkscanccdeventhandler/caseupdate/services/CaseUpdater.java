package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdateRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.ArrayList;
import java.util.List;

import static org.slf4j.LoggerFactory.getLogger;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.CONTACT_NUMBER;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.DATE_OF_BIRTH;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.EMAIL;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.FIRST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LAST_NAME;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.LEGACY_ID;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.OcrFieldExtractor.get;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.ScannedDocumentMapper.mapToScannedDocument;

@Service
public class CaseUpdater {

    public static final String EVENT_ID = "attachScannedDocs";

    private static final Logger LOG = getLogger(CaseUpdater.class);

    private final CaseUpdateDetailsValidator caseUpdateDetailsValidator;
    private final AddressExtractor addressExtractor;

    public CaseUpdater(
        CaseUpdateDetailsValidator caseUpdateDetailsValidator,
        AddressExtractor addressExtractor
    ) {
        this.caseUpdateDetailsValidator = caseUpdateDetailsValidator;
        this.addressExtractor = addressExtractor;
    }

    public SuccessfulUpdateResponse update(CaseUpdateRequest caseUpdateRequest) {
        Assert.notNull(
            caseUpdateRequest.caseUpdateDetails,
            "Case update details is required"
        );

        Assert.notEmpty(
            caseUpdateRequest.caseUpdateDetails.scannedDocuments,
            "Missing scanned documents"
        );

        final List<OcrDataField> ocrDataFields = caseUpdateRequest.caseUpdateDetails.ocrDataFields;

        Address newAddress = addressExtractor.extractFrom(ocrDataFields);

        final List<String> warnings = caseUpdateDetailsValidator.getWarnings(caseUpdateRequest.caseUpdateDetails);

        if (caseUpdateRequest.isAutomatedProcess && !warnings.isEmpty()) {
            throw new InvalidCaseUpdateDetailsException(warnings);
        }

        LOG.info("Case update, case details id: {}", caseUpdateRequest.caseDetails.id);

        SampleCase originalCase = caseUpdateRequest.caseDetails.caseData;
        // This is just a sample implementation, we only overwrite the address here.
        // You'll probably update other fields and add new documents in your service case.
        SampleCase newCase = new SampleCase(
            getOcrDataField(ocrDataFields, LEGACY_ID, originalCase.legacyId),
            getOcrDataField(ocrDataFields, FIRST_NAME, originalCase.firstName),
            getOcrDataField(ocrDataFields, LAST_NAME, originalCase.lastName),
            getOcrDataField(ocrDataFields, DATE_OF_BIRTH, originalCase.dateOfBirth),
            getOcrDataField(ocrDataFields, CONTACT_NUMBER, originalCase.contactNumber),
            getOcrDataField(ocrDataFields, EMAIL, originalCase.email),
            newAddress,
            mergeScannedDocuments(
                originalCase.scannedDocuments,
                caseUpdateRequest.caseUpdateDetails.scannedDocuments
            ),
            originalCase.bulkScanCaseReference
        );

        return new SuccessfulUpdateResponse(
            new CaseUpdateDetails(
                // This is just a sample implementation.
                // You can use different event IDs based on the changes made to a case...
                EVENT_ID,
                newCase
            ),
            warnings
        );
    }

    public String getOcrDataField(
        List<OcrDataField> ocrDataFields,
        String fieldName,
        String existingValue
    ) {
        String newValue = get(ocrDataFields, fieldName);
        return newValue == null ? existingValue : newValue;
    }

    private List<Item<ScannedDocument>> mergeScannedDocuments(
        List<Item<ScannedDocument>> caseScannedDocuments,
        List<InputScannedDoc> exceptionScannedDocuments
    ) {
        List<Item<ScannedDocument>> newScannedDocuments;
        if (caseScannedDocuments == null) {
            newScannedDocuments = new ArrayList<>();
        } else {
            newScannedDocuments = new ArrayList<>(caseScannedDocuments);
        }

        exceptionScannedDocuments.forEach(scannedDoc ->
            newScannedDocuments.add(new Item<>(mapToScannedDocument(scannedDoc)))
        );

        return newScannedDocuments;
    }
}
