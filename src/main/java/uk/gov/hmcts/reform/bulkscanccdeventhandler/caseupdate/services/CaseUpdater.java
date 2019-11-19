package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdate;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

import static java.util.Collections.emptyList;

@Service
public class CaseUpdater {

    public static final String EVENT_ID = "SAMPLE_EVENT_ID";

    private final AddressExtractor addressExtractor;

    public CaseUpdater(AddressExtractor addressExtractor) {
        this.addressExtractor = addressExtractor;
    }

    public SuccessfulUpdateResponse update(CaseUpdate caseUpdate) {
        Address newAddress = addressExtractor.extractFrom(caseUpdate.exceptionRecord.ocrDataFields);

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
            originalCase.scannedDocuments,
            originalCase.bulkScanCaseReference
        );

        return new SuccessfulUpdateResponse(
            new CaseUpdateDetails(
                // This is just a sample implementation.
                // You can use different event IDs based on the changes made to a case.
                EVENT_ID,
                newCase
            ),
            // This is just a sample implementation, put any warnings for the case worker here.
            emptyList()
        );
    }
}
