package uk.gov.hmcts.reform.bulkscanccdeventhandler.model;

import java.time.LocalDate;
import java.util.List;

public class CcdCase {

    public final String legacyId;
    public final String firstName;
    public final String lastName;
    public final LocalDate dateOfBirth;
    public final String contactNumber;
    public final String email;
    public final Address address;
    public final List<CcdCollectionElement<ScannedDocument>> scannedDocuments;

    public CcdCase(
        String legacyId,
        String firstName,
        String lastName,
        LocalDate dateOfBirth,
        String contactNumber,
        String email,
        Address address,
        List<CcdCollectionElement<ScannedDocument>> scannedDocuments
    ) {
        this.legacyId = legacyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.scannedDocuments = scannedDocuments;
    }
}
