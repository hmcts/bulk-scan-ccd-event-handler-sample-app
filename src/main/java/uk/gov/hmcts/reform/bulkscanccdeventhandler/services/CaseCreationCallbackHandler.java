package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.ExceptionRecordEventHandler;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationRequest;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.CaseCreationResult;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.handler.model.ExceptionRecord;
import uk.gov.hmcts.reform.ccd.client.model.CaseDetails;

@Service
public class CaseCreationCallbackHandler {

    private static final Logger log = LoggerFactory.getLogger(CaseCreationCallbackHandler.class);

    private static final String JURISDICTION = "BULKSCAN";

    private final ExceptionRecordEventHandler exceptionRecordEventHandler;

    public CaseCreationCallbackHandler(ExceptionRecordEventHandler exceptionRecordEventHandler) {
        this.exceptionRecordEventHandler = exceptionRecordEventHandler;
    }

    public CaseCreationResult handleCaseCreationCallback(
        CaseDetails exceptionRecordDetails,
        String eventId,
        String idamToken,
        String idamUserId,
        boolean ignoreWarnings
    ) {
        String exceptionRecordId = exceptionRecordDetails.getId() != null
            ? exceptionRecordDetails.getId().toString()
            : null;

        ExceptionRecord exceptionRecord = new ExceptionRecord(
            exceptionRecordId,
            exceptionRecordDetails.getJurisdiction(),
            exceptionRecordDetails.getState(),
            exceptionRecordDetails.getCaseTypeId(),
            exceptionRecordDetails.getData()
        );

        CaseCreationRequest caseCreationRequest = new CaseCreationRequest(
            exceptionRecord,
            eventId,
            idamToken,
            idamUserId,
            ignoreWarnings
        );

        logAboutToHandleCallback(exceptionRecord, eventId, idamUserId, ignoreWarnings);
        CaseCreationResult result = exceptionRecordEventHandler.handle(caseCreationRequest);
        logCallbackHandlingResult(result, exceptionRecord, ignoreWarnings);

        return result;
    }

    private void logCallbackHandlingResult(
        CaseCreationResult result,
        ExceptionRecord exceptionRecord,
        boolean ignoreWarnings
    ) {
        if (result.caseId != null) {
            log.info(
                "Created case from exception record. Case ID {}. Exception record ID {}.",
                result.caseId,
                exceptionRecord.id
            );
        } else {
            log.warn(
                "Rejected callback request for exception record {}.\nErrors: {}\nWarnings: {}\nIgnore warnings: {}",
                result.errors,
                result.warnings,
                ignoreWarnings
            );
        }
    }

    private void logAboutToHandleCallback(
        ExceptionRecord exceptionRecord,
        String eventId,
        String idamUserId,
        boolean ignoreWarnings
    ) {
        String logMessage =
            "About to handle case creation callback request. "
                + "Exception record ID: {}, jurisdiction: {}, event ID: {}, "
                + "case type: {}, user: {}, ignore warnings: {}";

        log.info(
            logMessage,
            exceptionRecord.id,
            exceptionRecord.jurisdiction,
            eventId,
            exceptionRecord.caseTypeId,
            idamUserId,
            ignoreWarnings
        );
    }
}
