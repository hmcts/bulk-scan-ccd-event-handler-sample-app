package uk.gov.hmcts.reform.bulkscanccdeventhandler;

import io.restassured.response.Response;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(SpringExtension.class)
@TestPropertySource("classpath:application.conf")
public class UpdateCaseTest {

    private final TestHelper testHelper = new TestHelper();

    @Test
    public void should_update_case() {

        // given
        byte[] request = TestHelper.fileContentAsBytes("updatecase/valid-request.json");

        // when
        Response response = testHelper.postWithBody("/update-case", request);

        // then
        assertThat(response.getStatusCode()).isEqualTo(OK.value());
        assertThat(response.getBody().jsonPath().getList("warnings")).isEmpty();
    }
}
