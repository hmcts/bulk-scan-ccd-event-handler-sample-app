package uk.gov.hmcts.reform.bulkscanccdeventhandler.pact;

import au.com.dius.pact.provider.junit.Provider;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;

@Provider("sample_app_ocr_validation")
class OcrValidationContractTest extends ContractTestSuite {

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    public void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }
}
