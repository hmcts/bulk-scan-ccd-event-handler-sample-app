package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.junit.jupiter.api.Test;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.in.OcrDataField;

import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_1;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_2;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.ADDRESS_LINE_3;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTRY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.COUNTY;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_CODE;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames.POST_TOWN;

public class AddressExtractorTest {

    private final AddressExtractor service = new AddressExtractor();

    @Test
    public void should_extract_address_model_from_exception_record_ocr_data() {
        // given
        List<OcrDataField> ocrData =
            asList(
                new OcrDataField(ADDRESS_LINE_1, "line1"),
                new OcrDataField(ADDRESS_LINE_2, "line2"),
                new OcrDataField(ADDRESS_LINE_3, "line3"),
                new OcrDataField(POST_CODE, "post code"),
                new OcrDataField(POST_TOWN, "post town"),
                new OcrDataField(COUNTY, "county"),
                new OcrDataField(COUNTRY, "country")
            );

        // when
        Address result = service.extractFrom(ocrData);

        // then
        assertThat(result.addressLine1).isEqualTo("line1");
        assertThat(result.addressLine2).isEqualTo("line2");
        assertThat(result.addressLine3).isEqualTo("line3");
        assertThat(result.postCode).isEqualTo("post code");
        assertThat(result.postTown).isEqualTo("post town");
        assertThat(result.county).isEqualTo("county");
        assertThat(result.country).isEqualTo("country");
    }

    @Test
    public void should_fill_missing_fields_with_nulls() {
        // given
        List<OcrDataField> ocrData =
            asList(
                new OcrDataField(ADDRESS_LINE_1, "line1"),
                new OcrDataField(ADDRESS_LINE_2, "line2"),
                new OcrDataField(ADDRESS_LINE_3, "line3")
            );

        // when
        Address result = service.extractFrom(ocrData);

        // then
        assertThat(result.addressLine1).isEqualTo("line1");
        assertThat(result.addressLine2).isEqualTo("line2");
        assertThat(result.addressLine3).isEqualTo("line3");
        assertThat(result.postCode).isNull();
        assertThat(result.postTown).isNull();
        assertThat(result.county).isNull();
        assertThat(result.country).isNull();
    }

    @Test
    public void should_return_empty_object_when_all_fields_are_missing() {
        // given
        List<OcrDataField> ocrData = Collections.emptyList();

        // when
        Address result = service.extractFrom(ocrData);

        // then
        assertThat(result.addressLine1).isNull();
        assertThat(result.addressLine2).isNull();
        assertThat(result.addressLine3).isNull();
        assertThat(result.postCode).isNull();
        assertThat(result.postTown).isNull();
        assertThat(result.county).isNull();
        assertThat(result.country).isNull();
    }
}
