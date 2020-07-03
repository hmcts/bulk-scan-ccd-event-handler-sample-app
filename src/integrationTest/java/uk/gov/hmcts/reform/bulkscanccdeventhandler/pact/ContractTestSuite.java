package uk.gov.hmcts.reform.bulkscanccdeventhandler.pact;

import au.com.dius.pact.provider.junit.loader.PactBroker;
import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.auth.AuthService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@PactBroker(host = "pact-broker.platform.hmcts.net", port = "443", scheme = "https")
@SpringBootTest(webEnvironment = RANDOM_PORT)
abstract class ContractTestSuite {

    @LocalServerPort
    private int port;

    @MockBean
    private AuthService authService;

    @Value("${pact.verifier.publishResults}")
    private String publishResults;

    @BeforeEach
    public void setupTestTarget(PactVerificationContext context) {
        System.getProperties().setProperty("pact.verifier.publishResults", publishResults);
        context.setTarget(new HttpTestTarget("localhost", port, "/"));
        when(authService.authenticate(any())).thenReturn("bulk_scan_sample_app_test");
        doNothing().when(authService).assertIsAllowedService(any());
    }

    public abstract void pactVerificationTestTemplate(PactVerificationContext context);
}
