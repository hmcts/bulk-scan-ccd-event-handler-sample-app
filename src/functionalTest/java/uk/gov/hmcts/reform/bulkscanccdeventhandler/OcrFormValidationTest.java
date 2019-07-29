package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.mapper.ObjectMapperType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.OcrValidationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.model.out.ValidationStatus;

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

    @BeforeAll
    public void setUp() {
        this.config = ConfigFactory.load();
        this.testUrl = config.getString("test-url");
        this.s2sUrl = config.getString("test-s2s-url");
        this.s2sName = config.getString("test-s2s-name");
        this.s2sSecret = config.getString("test-s2s-secret");
    }

    @Test
    void should_validate_ocr_data_and_return_success() throws Exception {
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
