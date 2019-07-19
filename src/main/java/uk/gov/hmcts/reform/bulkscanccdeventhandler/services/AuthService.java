package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.UnauthenticatedException;

import static java.util.Arrays.asList;
import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class AuthService {

    private final AuthTokenValidator authTokenValidator;
    private final String[] allowedServices;

    @Autowired
    public AuthService(
        AuthTokenValidator authTokenValidator,
        @Value("${allowed-services}") String[] allowedServices) {
        this.authTokenValidator = authTokenValidator;
        this.allowedServices = allowedServices;
    }

    public String authenticate(String authHeader) {
        if (isBlank(authHeader)) {
            throw new UnauthenticatedException("Provided S2S token is missing or invalid");
        } else {
            return authTokenValidator.getServiceName(authHeader);
        }
    }

    public void assertIsAllowedService(String serviceName) {
        if (!asList(allowedServices).contains(serviceName)) {
            throw new ForbiddenException("S2S token is not authorized to use the service");
        }
    }
}
