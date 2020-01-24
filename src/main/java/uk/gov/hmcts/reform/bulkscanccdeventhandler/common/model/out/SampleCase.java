package uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class SampleCase {

    public final String legacyId;
    public final String firstName;
    public final String lastName;
    public final String dateOfBirth;
    public final String contactNumber;
    public final String email;
    public final Address address;
    public final Item<List<ScannedDocument>> scannedDocuments;
    public final String bulkScanCaseReference;

    public SampleCase(
        @JsonProperty("legacyId") String legacyId,
        @JsonProperty("firstName") String firstName,
        @JsonProperty("lastName") String lastName,
        @JsonProperty("dateOfBirth") String dateOfBirth,
        @JsonProperty("contactNumber") String contactNumber,
        @JsonProperty("email") String email,
        @JsonProperty("address") Address address,
        @JsonProperty("scannedDocuments") Item<List<ScannedDocument>> scannedDocuments,
        @JsonProperty("bulkScanCaseReference") String bulkScanCaseReference
    ) {
        this.legacyId = legacyId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.contactNumber = contactNumber;
        this.email = email;
        this.address = address;
        this.scannedDocuments = scannedDocuments;
        this.bulkScanCaseReference = bulkScanCaseReference;
    }
}
