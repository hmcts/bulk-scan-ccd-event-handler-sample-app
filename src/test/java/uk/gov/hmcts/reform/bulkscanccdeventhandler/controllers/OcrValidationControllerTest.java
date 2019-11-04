package uk.gov.hmcts.reform.bulkscanccdeventhandler.controllers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.UnauthenticatedException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.controllers.OcrValidationController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.FormType;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrDataValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.OcrValidationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.services.exceptions.FormNotFoundException;

import java.io.IOException;

import static java.util.Collections.emptyList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.ocrvalidation.model.out.ValidationStatus.SUCCESS;

@WebMvcTest(OcrValidationController.class)
class OcrValidationControllerTest {

    @Autowired
    private transient MockMvc mockMvc;

    @MockBean
    private OcrDataValidator ocrDataValidator;

    @MockBean
    private AuthService authService;

    @Test
    void should_return_401_status_when_auth_service_throws_unauthenticated_exception() throws Exception {
        String requestBody = readResource("ocr-data/valid/valid-ocr-data.json");
        given(authService.authenticate("")).willThrow(UnauthenticatedException.class);

        mockMvc
            .perform(
                post("/forms/PERSONAL/validate-ocr")
                    .header("ServiceAuthorization", "")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(requestBody)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_401_status_when_auth_service_throws_invalid_token_exception() throws Exception {
        String requestBody = readResource("ocr-data/valid/valid-ocr-data.json");
        given(authService.authenticate("test-token")).willThrow(InvalidTokenException.class);

        mockMvc
            .perform(
                post("/forms/PERSONAL/validate-ocr")
                    .header("ServiceAuthorization", "test-token")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(requestBody)
            )
            .andExpect(status().isUnauthorized());
    }

    @Test
    void should_return_403_status_when_auth_service_throws_forbidden_exception() throws Exception {
        String requestBody = readResource("ocr-data/valid/valid-ocr-data.json");
        given(authService.authenticate(any())).willThrow(ForbiddenException.class);

        mockMvc
            .perform(
                post("/forms/PERSONAL/validate-ocr")
                    .header("ServiceAuthorization", "test-token")
                    .contentType(APPLICATION_JSON_VALUE)
                    .content(requestBody)
            )
            .andExpect(status().isForbidden())
            .andExpect(content().json("{\"error\":\"S2S token is not authorized to use the service\"}"));
    }

    @Test
    void should_return_success_message_when_ocr_data_is_valid() throws Exception {
        String requestBody = readResource("ocr-data/valid/valid-ocr-data.json");

        given(authService.authenticate("testServiceAuthHeader")).willReturn("testServiceName");
        given(ocrDataValidator.validate(eq(FormType.PERSONAL), any()))
            .willReturn(new OcrValidationResult(emptyList(), emptyList(), SUCCESS));

        mockMvc
            .perform(
                post("/forms/PERSONAL/validate-ocr")
                    .contentType(APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(requestBody)
            )
            .andExpect(status().isOk())
            .andExpect(content().contentType(APPLICATION_JSON_VALUE))
            .andExpect(content().json(readResource("ocr-data/response/valid-ocr-response.json")));
    }

    @Test
    void should_return_bad_request_with_ocr_fields_are_missing() throws Exception {
        given(authService.authenticate("testServiceAuthHeader")).willReturn("testServiceName");
        mockMvc
            .perform(
                post("/forms/PERSONAL/validate-ocr")
                    .contentType(APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/invalid/missing-ocr-fields.json"))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_bad_request_when_ocr_fields_list_is_empty() throws Exception {
        given(authService.authenticate("testServiceAuthHeader")).willReturn("testServiceName");
        mockMvc
            .perform(
                post("/forms/PERSONAL/validate-ocr")
                    .contentType(APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/invalid/empty-ocr-fields.json"))
            )
            .andExpect(status().isBadRequest());
    }

    @Test
    void should_return_form_not_found_exception_when_form_type_is_invalid() throws Exception {
        given(authService.authenticate("testServiceAuthHeader")).willReturn("testServiceName");
        MvcResult mvcResult = mockMvc
            .perform(
                post("/forms/invalid-form-type/validate-ocr")
                    .contentType(APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/invalid/invalid-form-type.json"))
            )
            .andExpect(status().isNotFound())
            .andExpect(content().json("{\"error\":\"Form type 'invalid-form-type' not found\"}"))
            .andReturn();

        assertThat(mvcResult.getResolvedException())
            .isInstanceOf(FormNotFoundException.class)
            .hasMessageContaining("Form type 'invalid-form-type' not found");
    }

    @Test
    void should_return_form_not_found_exception_when_form_type_case_does_not_match() throws Exception {
        given(authService.authenticate("testServiceAuthHeader")).willReturn("testServiceName");
        MvcResult mvcResult = mockMvc
            .perform(
                post("/forms/Personal/validate-ocr") //only PERSONAL is valid form type
                    .contentType(APPLICATION_JSON_VALUE)
                    .header("ServiceAuthorization", "testServiceAuthHeader")
                    .content(readResource("ocr-data/invalid/invalid-form-type.json"))
            ).andReturn();

        assertThat(mvcResult.getResolvedException())
            .isInstanceOf(FormNotFoundException.class)
            .hasMessageContaining("Form type 'Personal' not found");
    }

    private String readResource(final String fileName) throws IOException {
        return Resources.toString(Resources.getResource(fileName), Charsets.UTF_8);
    }
}
