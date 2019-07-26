package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;

@TestPropertySource("classpath:application.conf")
class OcrFormValidationTest {

    @Value("${test-url}")
    private String testUrl;

    @Value("${s2s-url}")
    private String s2sUrl;

    @Value("${s2s-name}")
    private String s2sName;

    @Value("${s2s-secret}")
    private String s2sSecret;

    private TestHelper testHelper = new TestHelper();

    @Test
    void should_validate_ocr_data_and_return_success() {
        Response response = RestAssured
            .given()
            .relaxedHTTPSValidation()
            .header("ServiceAuthorization", "Bearer " + testHelper.s2sSignIn(s2sName, s2sSecret, s2sUrl))
            .header(CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .baseUri(testUrl)
            .body(testHelper.fileContentAsBytes("valid-personal-ocr-data.json"))
            .when()
            .post("/forms/PERSONAL/validate-ocr")
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);

        OcrValidationResponse result = response.getBody().as(OcrValidationResponse.class, ObjectMapperType.JACKSON_2);

        assertThat(result.status).isEqualTo(ValidationStatus.SUCCESS);
        assertThat(result.errors).isEmpty();
        assertThat(result.warnings).isEmpty();

    }
}
