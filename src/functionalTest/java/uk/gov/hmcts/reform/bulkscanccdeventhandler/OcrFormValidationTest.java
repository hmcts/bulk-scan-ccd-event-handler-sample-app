package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
class OcrFormValidationTest {

    private String testUrl;

    private String s2sUrl;

    private String s2sName;

    private String s2sSecret;

    private Config config;

    private TestHelper testHelper = new TestHelper();

    @BeforeEach
    public void setUp() {
        this.config = ConfigFactory.load();
        this.testUrl = config.getString("test-url");
        this.s2sUrl = config.getString("test-s2s-url");
        this.s2sName = config.getString("test-s2s-name");
        this.s2sSecret = config.getString("test-s2s-secret");
    }

    @Test
    void should_validate_ocr_data_and_return_success() {
        Response response = sendOcrFormValidationRequest("PERSONAL", "valid-ocr-form-data.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        OcrValidationResponse expectedResponse = new OcrValidationResponse(
            emptyList(), emptyList(), ValidationStatus.SUCCESS
        );

        assertThat(validationResponse).isEqualToComparingFieldByFieldRecursively(expectedResponse);
    }

    @Test
    void should_return_errors_when_mandatory_fields_are_missing() {
        Response response = sendOcrFormValidationRequest("PERSONAL", "missing-mandatory-fields.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        OcrValidationResponse expectedResponse = new OcrValidationResponse(
            emptyList(),
            singletonList("first_name is missing"),
            ValidationStatus.ERRORS
        );

        assertThat(validationResponse).isEqualToComparingFieldByFieldRecursively(expectedResponse);
    }

    @Test
    void should_return_errors_when_optional_fields_are_missing() {
        Response response = sendOcrFormValidationRequest("CONTACT", "missing-optional-fields.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        OcrValidationResponse expectedResponse = new OcrValidationResponse(
            asList("post_tows is missing", "county is missing"),
            emptyList(),
            ValidationStatus.WARNINGS
        );

        assertThat(validationResponse).isEqualToComparingFieldByFieldRecursively(expectedResponse);
    }

    @Test
    void should_return_errors_when_additional_validations_are_failing() {
        Response response = sendOcrFormValidationRequest("CONTACT", "invalid-form-data.json");

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse validationResponse = response.getBody()
            .as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        OcrValidationResponse expectedResponse = new OcrValidationResponse(
            emptyList(),
            asList("Invalid email address", "Invalid phone number"),
            ValidationStatus.ERRORS
        );

        assertThat(validationResponse).isEqualToComparingFieldByFieldRecursively(expectedResponse);
    }

    private Response sendOcrFormValidationRequest(String formType, String fileName) {
        return RestAssured
            .given()
            .relaxedHTTPSValidation()
            .header("ServiceAuthorization", "Bearer " + testHelper.s2sSignIn(s2sName, s2sSecret, s2sUrl))
            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .baseUri(testUrl)
            .body(testHelper.fileContentAsBytes(fileName))
            .when()
            .post("/forms/" + formType + "/validate-ocr")
            .andReturn();
    }
}
