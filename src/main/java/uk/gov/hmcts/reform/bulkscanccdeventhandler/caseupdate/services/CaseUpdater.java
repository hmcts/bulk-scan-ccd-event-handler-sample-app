package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdate;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.InputScannedDoc;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.ScannedDocumentMapper.mapToScannedDocument;

@Service
public class CaseUpdater {

    public static final String EVENT_ID = "attachScannedDocs";

    private static final Logger LOG = getLogger(CaseUpdater.class);

    private final AddressExtractor addressExtractor;

    public CaseUpdater(AddressExtractor addressExtractor) {
        this.addressExtractor = addressExtractor;
    }

    public SuccessfulUpdateResponse update(CaseUpdate caseUpdate) {
        Assert.notEmpty(
            caseUpdate.exceptionRecord.scannedDocuments,
            "Missing scanned documents in exception record"
        );

        Address newAddress = addressExtractor.extractFrom(caseUpdate.exceptionRecord.ocrDataFields);

        LOG.info("Case update, case details id: {}", caseUpdate.caseDetails.id);

        SampleCase originalCase = caseUpdate.caseDetails.caseData;
        // This is just a sample implementation, we only overwrite the address here.
        // You'll probably update other fields and add new documents in your service case.
        SampleCase newCase = new SampleCase(
            originalCase.legacyId,
            originalCase.firstName,
            originalCase.lastName,
            originalCase.dateOfBirth,
            originalCase.contactNumber,
            originalCase.email,
            newAddress,
            mergeScannedDocuments(originalCase.scannedDocuments, caseUpdate.exceptionRecord.scannedDocuments),
            originalCase.bulkScanCaseReference
        );

        return new SuccessfulUpdateResponse(
            new CaseUpdateDetails(
                // This is just a sample implementation.
                // You can use different event IDs based on the changes made to a case...
                EVENT_ID,
                newCase
            ),
            // ... and put any warnings here.
            emptyList()
        );
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
