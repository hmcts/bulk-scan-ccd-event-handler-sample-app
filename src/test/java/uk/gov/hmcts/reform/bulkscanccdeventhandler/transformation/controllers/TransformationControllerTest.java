package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.controllers;

import com.microsoft.applicationinsights.web.dependencies.apachecommons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.UnauthenticatedException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.CaseCreationDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.out.SuccessfulTransformationResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services.ExceptionRecordToCaseTransformer;

import java.time.LocalDateTime;
import java.util.List;

import static java.time.format.DateTimeFormatter.ISO_DATE_TIME;
import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("checkstyle:lineLength")
@WebMvcTest(TransformationController.class)
public class TransformationControllerTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private ExceptionRecordToCaseTransformer transformer;
    @MockBean private AuthService authService;

    @Test
    public void should_return_proper_status_codes_for_auth_exceptions() throws Exception {
        List<Pair<RuntimeException, HttpStatus>> exceptionsAndStatuses =
            asList(
                Pair.of(new UnauthenticatedException(null), UNAUTHORIZED),
                Pair.of(new InvalidTokenException(null, null), UNAUTHORIZED),
                Pair.of(new ForbiddenException(null), FORBIDDEN)
            );

        for (Pair<RuntimeException, HttpStatus> pair : exceptionsAndStatuses) {
            Mockito.reset(authService);
            given(authService.authenticate(any())).willThrow(pair.getLeft());

            sendRequest("{}")
                .andExpect(status().is(pair.getRight().value()));
        }
    }

    @Test
    void should_return_case_data_if_transformation_succeeded() throws Exception {
        SuccessfulTransformationResponse transformationResult =
            new SuccessfulTransformationResponse(
                new CaseCreationDetails(
                    "case-type-id",
                    "event-id",
                    new SampleCase(
                        "legacy-id",
                        "first-name",
                        "last-name",
                        "date-of-birth",
                        "contact-number",
                        "email",
                        new Address(
                            "address-line-1",
                            "address-line-2",
                            "address-line-3",
                            "post-code",
                            "post-town",
                            "county",
                            "country"
                        ),
                        asList(
                            new ScannedDocument(
                                "type-1",
                                "subtype-1",
                                "url-1",
                                "dcn-1",
                                "file-name-1",
                                LocalDateTime.parse("2011-12-03T10:15:30.123", ISO_DATE_TIME),
                                LocalDateTime.parse("2011-12-04T10:15:30.123", ISO_DATE_TIME),
                                "ref-1"
                            ),
                            new ScannedDocument(
                                "type-2",
                                "subtype-2",
                                "url-2",
                                "dcn-2",
                                "file-name-2",
                                LocalDateTime.parse("2011-12-05T10:15:30.123", ISO_DATE_TIME),
                                LocalDateTime.parse("2011-12-06T10:15:30.123", ISO_DATE_TIME),
                                "ref-2"
                            )
                        )
                    )
                ),
                asList(
                    "warning-1",
                    "warning-2"
                )
            );

        given(transformer.toCase(any()))
            .willReturn(transformationResult);

        sendRequest("{}")
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.case_creation_details.case_type_id").value("case-type-id"))
            .andExpect(jsonPath("$.case_creation_details.event_id").value("event-id"))
            .andExpect(jsonPath("$.case_creation_details.case_data.legacyId").value("legacy-id"))
            .andExpect(jsonPath("$.case_creation_details.case_data.firstName").value("first-name"))
            .andExpect(jsonPath("$.case_creation_details.case_data.lastName").value("last-name"))
            .andExpect(jsonPath("$.case_creation_details.case_data.dateOfBirth").value("date-of-birth"))
            .andExpect(jsonPath("$.case_creation_details.case_data.contactNumber").value("contact-number"))
            .andExpect(jsonPath("$.case_creation_details.case_data.email").value("email"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.addressLine1").value("address-line-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.addressLine2").value("address-line-2"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.addressLine3").value("address-line-3"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.postCode").value("post-code"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.postTown").value("post-town"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.county").value("county"))
            .andExpect(jsonPath("$.case_creation_details.case_data.address.country").value("country"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].type").value("type-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].subtype").value("subtype-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].url").value("url-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].controlNumber").value("dcn-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].fileName").value("file-name-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].scannedDate").value("2011-12-03T10:15:30.123"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].deliveryDate").value("2011-12-04T10:15:30.123"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[0].exceptionRecordReference").value("ref-1"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].type").value("type-2"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].subtype").value("subtype-2"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].url").value("url-2"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].controlNumber").value("dcn-2"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].fileName").value("file-name-2"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].scannedDate").value("2011-12-05T10:15:30.123"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].deliveryDate").value("2011-12-06T10:15:30.123"))
            .andExpect(jsonPath("$.case_creation_details.case_data.scannedDocuments[1].exceptionRecordReference").value("ref-2"))
            .andExpect(jsonPath("$.warnings[0]").value("warning-1"))
            .andExpect(jsonPath("$.warnings[1]").value("warning-2"));
    }

    private ResultActions sendRequest(String body) throws Exception {
        return mockMvc
            .perform(
                post("/transform-exception-record")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(body)
            );
    }

}
