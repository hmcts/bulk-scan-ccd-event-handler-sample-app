package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

public class TestHelper {

    private final String testUrl;

    private final String s2sUrl;

    private final String s2sName;

    private final String s2sSecret;

    TestHelper() {
        Config config = ConfigFactory.load();

        this.testUrl = config.getString("test-url");
        this.s2sUrl = config.getString("test-s2s-url");
        this.s2sName = config.getString("test-s2s-name");
        this.s2sSecret = config.getString("test-s2s-secret");
    }

    protected String s2sSignIn() {
        Map<String, Object> params = ImmutableMap.of(
            "microservice", s2sName,
            "oneTimePassword", new GoogleAuthenticator().getTotpPassword(s2sSecret)
        );

        Response response = RestAssured
            .given()
            .relaxedHTTPSValidation()
            .baseUri(s2sUrl)
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .body(params)
            .when()
            .post("/lease")
            .andReturn();

        assertThat(response.getStatusCode()).isEqualTo(200);

        return response
            .getBody()
            .print();
    }

    protected byte[] fileContentAsBytes(String file) {
        try {
            return Resources.toByteArray(Resources.getResource(file));
        } catch (IOException e) {
            throw new RuntimeException("Could not load file " + file, e);
        }
    }

}
