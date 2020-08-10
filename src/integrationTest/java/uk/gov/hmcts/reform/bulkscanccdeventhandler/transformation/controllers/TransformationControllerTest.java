package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.controllers;

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
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class TransformationControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void should_respond_422_when_transformation_details_invalid() throws Exception {
        String body = Resources.toString(
            getResource("transformation/invalid-email-in-transformation-details.json"),
            UTF_8
        );

        mvc.perform(
            post("/transform-exception-record")
                .header("ServiceAuthorization", "auth-header-value")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .content(body)
        ).andExpect(
            status().is(UNPROCESSABLE_ENTITY.value())
        ).andExpect(
            jsonPath("$.errors").value("invalid email 'invalid'")
        );
    }
}
