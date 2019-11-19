package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services.CaseUpdater;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.UnauthenticatedException;

import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("checkstyle:lineLength")
@WebMvcTest(UpdateCaseController.class)
class UpdateCaseControllerTest {

    @Autowired private transient MockMvc mockMvc;

    @MockBean private CaseUpdater caseUpdater;
    @MockBean private AuthService authService;

    @BeforeEach
    void setUp() {
        Mockito.reset(authService);
    }

    @ParameterizedTest
    @MethodSource("exceptionsAndStatuses")
    public void should_return_proper_status_codes_for_auth_exceptions(RuntimeException exc, HttpStatus status) throws Exception {
        given(authService.authenticate(any())).willThrow(exc);

        sendRequest("{}")
            .andExpect(status().is(status.value()));
    }

    private static Stream<Arguments> exceptionsAndStatuses() {
        return Stream.of(
            Arguments.of(new UnauthenticatedException(null), UNAUTHORIZED),
            Arguments.of(new InvalidTokenException(null, null), UNAUTHORIZED),
            Arguments.of(new ForbiddenException(null), FORBIDDEN)
        );
    }

    private ResultActions sendRequest(String body) throws Exception {
        return mockMvc
            .perform(
                post("/update-case")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            );
    }
}
