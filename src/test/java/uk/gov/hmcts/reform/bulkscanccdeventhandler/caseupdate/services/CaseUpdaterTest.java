package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.ExceptionRecord;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.JourneyClassification;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CaseUpdaterTest {

    @Mock private AddressExtractor addressExtractor;

    private CaseUpdater caseUpdater;

    @BeforeEach
    void setUp() {
        this.caseUpdater = new CaseUpdater(addressExtractor);
    }

    @Test
    void should_overwrite_address_with_address_from_exception_record() {
        // given

        Address originalCaseAddress = new Address("a", "b", "c", "d", "e", "f", "g");
        Address exceptionRecordAddress = new Address("0", "1", "2", "3", "4", "5", "6");

        SampleCase originalCase = new SampleCase(
            "legacy-id",
            "first-name",
            "last-name",
            "date-of-birth",
            "contact-number",
            "email",
            originalCaseAddress,
            emptyList(),
            "er-id"
        );

        ExceptionRecord exceptionRecord = new ExceptionRecord(
            "er-id",
            "er-case-type",
            "er-pobox",
            "er-jurisdiction",
            "er-form-type",
            JourneyClassification.SUPPLEMENTARY_EVIDENCE_WITH_OCR,
            now(),
            now(),
            emptyList(),
            emptyList()
        );

        given(addressExtractor.extractFrom(any())).willReturn(exceptionRecordAddress);

        // when
        SampleCase updatedCase = caseUpdater.update(originalCase, exceptionRecord);

        // then
        assertThat(updatedCase.address.addressLine1).isEqualTo(exceptionRecordAddress.addressLine1);
        assertThat(updatedCase.address.addressLine2).isEqualTo(exceptionRecordAddress.addressLine2);
        assertThat(updatedCase.address.addressLine3).isEqualTo(exceptionRecordAddress.addressLine3);
        assertThat(updatedCase.address.country).isEqualTo(exceptionRecordAddress.country);
        assertThat(updatedCase.address.county).isEqualTo(exceptionRecordAddress.county);
        assertThat(updatedCase.address.postCode).isEqualTo(exceptionRecordAddress.postCode);
        assertThat(updatedCase.address.postTown).isEqualTo(exceptionRecordAddress.postTown);

        assertThat(updatedCase)
            .extracting(c -> tuple(
                c.legacyId,
                c.firstName,
                c.lastName,
                c.dateOfBirth,
                c.bulkScanCaseReference
            ))
            .isEqualTo(tuple(
                originalCase.legacyId,
                originalCase.firstName,
                originalCase.lastName,
                originalCase.dateOfBirth,
                originalCase.bulkScanCaseReference
            ));
    }
}
