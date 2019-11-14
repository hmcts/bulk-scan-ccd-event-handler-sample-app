package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

@Service
public class CaseUpdater {

    private final AddressExtractor addressExtractor;

    public CaseUpdater(AddressExtractor addressExtractor) {
        this.addressExtractor = addressExtractor;
    }

    public SampleCase update(SampleCase original, ExceptionRecord exceptionRecord) {
        Address newAddress = addressExtractor.extractFrom(exceptionRecord.ocrDataFields);

        // This is just a sample implementation, we only overwrite the address here.
        // You'll probably update other fields and add new documents in your service case.
        return new SampleCase(
            original.legacyId,
            original.firstName,
            original.lastName,
            original.dateOfBirth,
            original.contactNumber,
            original.email,
            newAddress,
            original.scannedDocuments,
            original.bulkScanCaseReference
        );
    }
}
