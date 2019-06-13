package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.typesafe.config.ConfigFactory;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExampleTest {

    private static final String TEST_URL = ConfigFactory.load().getString("test-url");

    @Test
    public void replaceThisWithActualTests() {
        // TODO: this test is there so that a test report can be created for functional tests
        // (otherwise the build fails). Remove when actual smoke tests have been written.
        Response response = RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(TEST_URL)
            .get("/health")
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);
    }
}
