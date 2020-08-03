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

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;
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
        // TODO remove this check after eliminating exception_record element from CaseUpdateRequest
        Assert.notEmpty(
            caseUpdateRequest.transformationInput.scannedDocuments,
            "Missing scanned documents in exception record"
        );

        if (caseUpdateRequest.isAutomatedProcess) {
            Assert.notNull(
                caseUpdateRequest.caseUpdateDetails,
                "Missing case update details when process is automated"
            );
        }

        final List<String> warnings =
            // TODO fix functional test to make it possible to remove check if caseUpdateDetails is null
            caseUpdateRequest.caseUpdateDetails == null
                ? emptyList()
                : caseUpdateDetailsValidator.getWarnings(caseUpdateRequest.caseUpdateDetails);

        Address newAddress = addressExtractor.extractFrom(caseUpdateRequest.transformationInput.ocrDataFields);

        if (caseUpdateRequest.isAutomatedProcess && !warnings.isEmpty()) {
            throw new InvalidCaseUpdateDetailsException(warnings);
        }

        LOG.info("Case update, case details id: {}", caseUpdateRequest.caseDetails.id);

        SampleCase originalCase = caseUpdateRequest.caseDetails.caseData;
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
            mergeScannedDocuments(
                originalCase.scannedDocuments,
                caseUpdateRequest.transformationInput.scannedDocuments
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
