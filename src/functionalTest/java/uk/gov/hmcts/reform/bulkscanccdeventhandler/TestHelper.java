package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

class TestHelper {
    String s2sSignIn(String s2sName, String s2sSecret, String s2sUrl) {

        System.out.println(
            String.format("s2sSignin:: s2sName:: %s \t s2sSecret:: %s \t s2sUrl:: %s", s2sName, s2sSecret, s2sUrl)
        );

        int s2sOtp = 0;
        if (StringUtils.isNotBlank(s2sSecret)) {
            s2sOtp = new GoogleAuthenticator().getTotpPassword(s2sSecret);
            System.out.println("S2s OTP:::" + s2sOtp);
        } else {
            System.out.println("S2s secret is Blank");
        }
        Map<String, Object> params = ImmutableMap.of(
            "microservice", s2sName,
            "oneTimePassword", s2sOtp
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

    byte[] fileContentAsBytes(String file) {
        try {
            return Resources.toByteArray(Resources.getResource(file));
        } catch (IOException e) {
            throw new RuntimeException("Could not load file" + file, e);
        }
    }

}
