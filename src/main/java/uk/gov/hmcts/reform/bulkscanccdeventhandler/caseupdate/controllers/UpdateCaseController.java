package uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.controllers;

import org.slf4j.Logger;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.in.CaseUpdate;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.model.out.SuccessfulUpdateResponse;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.caseupdate.services.CaseUpdater;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;

import javax.validation.Valid;

import static org.slf4j.LoggerFactory.getLogger;

@RestController
public class UpdateCaseController {

    private static final Logger LOG = getLogger(UpdateCaseController.class);

    private final AuthService authService;
    private final CaseUpdater caseUpdater;

    // region constructor
    public UpdateCaseController(
        AuthService authService,
        CaseUpdater caseUpdater
    ) {
        this.authService = authService;
        this.caseUpdater = caseUpdater;
    }
    // endregion

    @PostMapping("/update-case")
    public SuccessfulUpdateResponse updateCase(
        @RequestHeader(name = "ServiceAuthorization", required = false) String serviceAuthHeader,
        @Valid @RequestBody CaseUpdate req
    ) {
        String serviceName = authService.authenticate(serviceAuthHeader);
        LOG.info("Request received to update case with OCR from service {}", serviceName);

        authService.assertIsAllowedService(serviceName);

        return caseUpdater.update(req);
    }
}
