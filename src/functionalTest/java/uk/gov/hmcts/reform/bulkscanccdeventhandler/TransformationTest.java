package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
public class TransformationTest {

    private final TestHelper testHelper = new TestHelper();

    @Test
    public void should_transform_exception_record_successfully_without_any_warnings() {
        Response response = testHelper.postWithBody(
            "/transform-exception-record",
            TestHelper.fileContentAsBytes("exception-records/valid.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(OK.value());

        JsonPath transformationResponse = response.getBody().jsonPath();

        assertThat(transformationResponse.getList("warnings")).isEmpty();

        Map<String, Object> details = transformationResponse.getMap("case_creation_details");

        assertThat(details.get("case_type_id")).isEqualTo(ExceptionRecordToCaseTransformer.CASE_TYPE_ID);
        assertThat(details.get("event_id")).isEqualTo(ExceptionRecordToCaseTransformer.EVENT_ID);

        Map<String, Object> caseData = (Map<String, Object>) details.get("case_data");

        assertThat(caseData.get("legacyId")).isNull();
        assertThat(caseData.get("firstName")).isEqualTo("FIRST");
        assertThat(caseData.get("lastName")).isEqualTo("NAME");
        assertThat(caseData.get("dateOfBirth")).isNull();
        assertThat(caseData.get("contactNumber")).isNull();
        assertThat(caseData.get("email")).isEqualTo("non-empty-email");

        Map<String, Object> address = (Map<String, Object>) caseData.get("address");

        assertThat(address.get("addressLine1")).isNull();
        assertThat(address.get("addressLine2")).isNull();
        assertThat(address.get("addressLine3")).isNull();
        assertThat(address.get("postCode")).isNull();
        assertThat(address.get("postTown")).isNull();
        assertThat(address.get("county")).isNull();
        assertThat(address.get("country")).isNull();

        assertThat(caseData.get("scannedDocuments"))
            .asList()
            .hasSize(1);

        Map<String, Object> document = ((List<Map<String, Map<String, Object>>>) caseData.get("scannedDocuments"))
            .get(0)
            .get("value");

        assertThat(document.get("type")).isEqualTo("Form");
        assertThat(document.get("subtype")).isEqualTo("XYZ");
        assertThat(document.get("url")).isEqualTo("url");
        assertThat(document.get("controlNumber")).isEqualTo("987654321");
        assertThat(document.get("fileName")).isEqualTo("987654321-123456789.pdf");
        assertThat(document.get("scannedDate")).isEqualTo("2019-08-01T00:01:02.345");
        assertThat(document.get("deliveryDate")).isEqualTo("2019-08-01T01:02:03.456");
        assertThat(document.get("exceptionRecordReference")).isEqualTo("id");
    }

    @Test
    public void should_transform_exception_record_successfully_with_warning() {
        Response response = testHelper.postWithBody(
            "/transform-exception-record",
            TestHelper.fileContentAsBytes("exception-records/valid-missing-email.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(OK.value());

        JsonPath transformationResponse = response.getBody().jsonPath();

        assertThat(transformationResponse.getList("warnings"))
            .hasSize(1)
            .containsOnly("'email' is empty");

        // full blow response is checked in happy path scenario above ^

        assertThat(transformationResponse.getMap("case_creation_details").get("email")).isNull();
    }

    @Test
    public void should_not_transform_exception_record_and_respond_with_422() {
        Response response = testHelper.postWithBody(
            "/transform-exception-record",
            TestHelper.fileContentAsBytes("exception-records/invalid-missing-last-name.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());

        JsonPath errorResponse = response.getBody().jsonPath();

        assertThat(errorResponse.getList("errors"))
            .hasSize(1)
            .containsOnly("'last_name' is required");
        assertThat(errorResponse.getList("warnings")).isEmpty();
        assertThat(errorResponse.getMap("")).containsOnlyKeys("errors", "warnings");
    }
}
