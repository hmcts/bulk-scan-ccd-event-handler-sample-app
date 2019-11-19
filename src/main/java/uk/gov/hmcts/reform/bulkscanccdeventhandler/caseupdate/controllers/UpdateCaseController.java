package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.controllers;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdate;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.CaseUpdateDetails;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services.CaseUpdater;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.out.SampleCase;

import javax.validation.Valid;

import static java.util.Collections.emptyList;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class UpdateCaseController {

    private static final Logger LOGGER = getLogger(UpdateCaseController.class);

    private final AuthService authService;
    private final CaseUpdater caseUpdater;

    public UpdateCaseController(
        AuthService authService,
        CaseUpdater caseUpdater
    ) {
        this.authService = authService;
        this.caseUpdater = caseUpdater;
    }

    @PostMapping("/update-case")
    public SuccessfulUpdateResponse updateCase(
        @RequestHeader(name = "ServiceAuthorization", required = false) String serviceAuthHeader,
        @Valid @RequestBody CaseUpdate req
    ) {
        String serviceName = authService.authenticate(serviceAuthHeader);
        LOGGER.info("Request received to update case. Service: {}", serviceName);

        authService.assertIsAllowedService(serviceName);

        SampleCase updatedCase = caseUpdater.update(req.caseDetails.caseData, req.exceptionRecord);

        return new SuccessfulUpdateResponse(
            new CaseUpdateDetails(updatedCase),
            emptyList()
        );
    }
}
