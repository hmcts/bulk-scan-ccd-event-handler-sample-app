package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services.CaseUpdater;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
public class UpdateCaseTest {

    private final TestHelper testHelper = new TestHelper();

    @Test
    public void should_update_case() throws JSONException {
        // given
        String request = TestHelper.fileContentAsString("updatecase/valid-request.json");

        // when
        Response response = testHelper.postWithBody("/update-case", request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK.value());
        JsonPath json = response.getBody().jsonPath();
        assertThat(json.getList("warnings")).isEmpty();
        assertThat(json.getMap("case_update_details").get("event_id")).isEqualTo(CaseUpdater.EVENT_ID);
        String expectedJson = TestHelper.fileContentAsString("updatecase/valid-response.json");
        JSONAssert.assertEquals(expectedJson, response.getBody().asString(), false);
    }
}
