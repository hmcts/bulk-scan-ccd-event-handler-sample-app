package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.controllers;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import uk.gov.hmcts.reform.authorisation.exceptions.InvalidTokenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services.CaseUpdater;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.UnauthenticatedException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Address;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.DocumentUrl;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.Item;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.ScannedDocument;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.AddressExtractor;

import java.time.LocalDateTime;
import java.util.stream.Stream;

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
@WebMvcTest({UpdateCaseController.class, AddressExtractor.class})
class UpdateCaseControllerTest {

    @Autowired private transient MockMvc mockMvc;

    @SpyBean private CaseUpdater caseUpdater;
    @MockBean private AuthService authService;
    @MockBean private AddressExtractor addressExtractor;

    @BeforeEach
    public void setUp() {
        Mockito.reset(authService);
    }

    @ParameterizedTest
    @MethodSource("exceptionsAndStatuses")
    public void should_return_proper_status_codes_for_auth_exceptions(RuntimeException exc, HttpStatus status) throws Exception {
        given(authService.authenticate(any())).willThrow(exc);

        sendRequest("{}")
            .andExpect(status().is(status.value()));
    }

    @Test
    public void should_return_updated_case_details() throws Exception {
        SampleCase sampleCase = new SampleCase(
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
                new Item<>(new ScannedDocument(
                    "type-1",
                    "subtype-1",
                    new DocumentUrl(
                        "url-1",
                        "binary-url-1",
                        "file-name-1"
                    ),
                    "dcn-1",
                    "file-name-1",
                    LocalDateTime.parse("2011-12-03T10:15:30.123", ISO_DATE_TIME),
                    LocalDateTime.parse("2011-12-04T10:15:30.123", ISO_DATE_TIME),
                    "ref-1"
                )),
                new Item<>(new ScannedDocument(
                    "type-2",
                    "subtype-2",
                    new DocumentUrl(
                        "url-2",
                        "binary-url-2",
                        "file-name-2"
                    ),
                    "dcn-2",
                    "file-name-2",
                    LocalDateTime.parse("2011-12-05T10:15:30.123", ISO_DATE_TIME),
                    LocalDateTime.parse("2011-12-06T10:15:30.123", ISO_DATE_TIME),
                    "ref-2"
                ))
            ),
            "er-id"
        );

        given(addressExtractor.extractFrom(any()))
            .willReturn(
                new Address("address-line-1","address-line-2","address-line-3",
                    "post-code","post-town","county","country"
                    )
            );

        String requestContent = Resources.toString(Resources.getResource("request/update-request.json"), Charsets.UTF_8);

        sendRequest(requestContent)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.case_update_details.event_id").value(CaseUpdater.EVENT_ID))
            .andExpect(jsonPath("$.case_update_details.case_data.legacyId").value("legacy-id"))
            .andExpect(jsonPath("$.case_update_details.case_data.firstName").value("first-name"))
            .andExpect(jsonPath("$.case_update_details.case_data.lastName").value("last-name"))
            .andExpect(jsonPath("$.case_update_details.case_data.dateOfBirth").value("date-of-birth"))
            .andExpect(jsonPath("$.case_update_details.case_data.contactNumber").value("contact-number"))
            .andExpect(jsonPath("$.case_update_details.case_data.email").value("email"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.addressLine1").value("address-line-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.addressLine2").value("address-line-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.addressLine3").value("address-line-3"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.postCode").value("post-code"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.postTown").value("post-town"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.county").value("county"))
            .andExpect(jsonPath("$.case_update_details.case_data.address.country").value("country"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.type").value("type-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.subtype").value("subtype-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.url.document_url").value("url-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.url.document_binary_url").value("binary-url-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.url.document_filename").value("file-name-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.controlNumber").value("dcn-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.fileName").value("file-name-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.scannedDate").value("2011-12-03T10:15:30.123"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.deliveryDate").value("2011-12-04T10:15:30.123"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[0].value.exceptionRecordReference").value("ref-1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.type").value("type-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.subtype").value("subtype-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.url.document_url").value("url-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.url.document_binary_url").value("binary-url-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.url.document_filename").value("file-name-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.controlNumber").value("dcn-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.fileName").value("file-name-2"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.scannedDate").value("2011-12-05T10:15:30.123"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.deliveryDate").value("2011-12-06T10:15:30.123"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[1].value.exceptionRecordReference").value("ref-2"))

            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[2].value.controlNumber").value("dcn1"))
            .andExpect(jsonPath("$.case_update_details.case_data.scannedDocuments[3].value.controlNumber").value("dcn2"))
            .andExpect(jsonPath("$.case_update_details.case_data.bulkScanCaseReference").value("er-id"));

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
