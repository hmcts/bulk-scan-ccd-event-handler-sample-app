package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
public class TransformationTest {

    public static final String TRANSFORM_EXCEPTION_RECORD_REQUEST_PATH = "/transform-exception-record";
    public static final String TRANSFORM_SCANNED_DATA_REQUEST_PATH = "/transform-scanned-data";

    private final TestHelper testHelper = new TestHelper();

    @Test
    public void should_transform_exception_record_successfully_without_any_warnings() {
        trasnformExceptionRecordAndVerifyResponse(TRANSFORM_EXCEPTION_RECORD_REQUEST_PATH, "exception-records/valid.json");
    }

    @Test
    public void should_transform_exception_record_with_auto_case_creation_fields_without_any_warnings() {
        trasnformExceptionRecordAndVerifyResponse(
            TRANSFORM_EXCEPTION_RECORD_REQUEST_PATH,
            "exception-records/valid-with-auto-case-creation-fields.json"
        );
    }

    @Test
    public void should_transform_exception_record_successfully_with_warning() {
        Response response = testHelper.postWithBody(
            TRANSFORM_EXCEPTION_RECORD_REQUEST_PATH,
            TestHelper.fileContentAsString("exception-records/valid-missing-email.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(OK.value());

        JsonPath transformationResponse = response.getBody().jsonPath();

        assertThat(transformationResponse.getList("warnings"))
            .hasSize(1)
            .containsOnly("'email' is empty");

        assertThat(transformationResponse.getMap("case_creation_details").get("email")).isNull();
    }

    @Test
    public void should_not_transform_exception_record_and_respond_with_422() {
        Response response = testHelper.postWithBody(
            TRANSFORM_EXCEPTION_RECORD_REQUEST_PATH,
            TestHelper.fileContentAsString("exception-records/invalid-missing-last-name.json")
        );

        assertThat(response.getStatusCode()).isEqualTo(UNPROCESSABLE_ENTITY.value());

        JsonPath errorResponse = response.getBody().jsonPath();

        assertThat(errorResponse.getList("errors"))
            .hasSize(1)
            .containsOnly("'last_name' is required");
        assertThat(errorResponse.getList("warnings")).isEmpty();
        assertThat(errorResponse.getMap("")).containsOnlyKeys("errors", "warnings");
    }

    @Test
    public void should_transform_auto_case_creation_request_data_successfully_without_any_warnings() {
        trasnformExceptionRecordAndVerifyResponse(
            TRANSFORM_SCANNED_DATA_REQUEST_PATH,
            "exception-records/valid-auto-case-creation.json"
        );
    }

    @Test
    public void should_transform_exception_data_successfully_without_any_warnings() {
        trasnformExceptionRecordAndVerifyResponse(
            TRANSFORM_SCANNED_DATA_REQUEST_PATH,
            "exception-records/valid-exception-record-request.json"
        );
    }

    @SuppressWarnings("unchecked")
    private void trasnformExceptionRecordAndVerifyResponse(String requestPath, String fileName) {
        Response response = testHelper.postWithBody(
            requestPath,
            TestHelper.fileContentAsString(fileName)
        );

        assertThat(response.getStatusCode()).isEqualTo(OK.value());

        JsonPath transformationResponse = response.getBody().jsonPath();

        assertSoftly(softly -> {
            softly.assertThat(transformationResponse.getList("warnings")).isEmpty();
            softly.assertThat(transformationResponse.getMap("case_creation_details").get("case_type_id"))
                .isEqualTo(ExceptionRecordToCaseTransformer.CASE_TYPE_ID);
            softly.assertThat(transformationResponse.getMap("case_creation_details").get("event_id"))
                .isEqualTo(ExceptionRecordToCaseTransformer.EVENT_ID);

            Map<String, Object> caseData = (Map<String, Object>) transformationResponse
                .getMap("case_creation_details")
                .get("case_data");

            softly.assertThat(caseData.get("email")).isEqualTo("hello@test.com");

            softly.assertAll();
        });
    }
}
