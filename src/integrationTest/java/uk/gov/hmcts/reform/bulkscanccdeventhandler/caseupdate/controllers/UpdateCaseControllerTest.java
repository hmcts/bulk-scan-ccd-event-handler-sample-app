package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.controllers;

import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static com.google.common.io.Resources.getResource;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class UpdateCaseControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void should_respond_400_when_required_scanned_documents_in_exception_records_are_missing() throws Exception {
        String body = Resources.toString(getResource("caseupdate/invalid-no-documents.json"), UTF_8);

        mvc.perform(
            post("/update-case")
                .header("ServiceAuthorization", "auth-header-value")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(body)
        ).andExpect(
            status().is(BAD_REQUEST.value())
        ).andExpect(
            jsonPath("$.error").value("Missing scanned documents in exception record")
        );
    }
}
