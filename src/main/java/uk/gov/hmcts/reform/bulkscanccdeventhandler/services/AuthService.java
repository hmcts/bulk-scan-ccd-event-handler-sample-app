package uk.gov.hmcts.reform.bulkscanccdeventhandler.services;

import com.google.common.base.Splitter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.authorisation.validators.AuthTokenValidator;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.ForbiddenException;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.services.exception.UnauthenticatedException;

import java.util.List;

import static org.apache.commons.lang.StringUtils.isBlank;

@Component
public class AuthService {

    private final AuthTokenValidator authTokenValidator;
    private final List<String> allowedServices;

    @Autowired
    public AuthService(
        AuthTokenValidator authTokenValidator,
        @Value("${allowed-services}") String allowedServices) {
        this.authTokenValidator = authTokenValidator;
        this.allowedServices = Splitter.on(",").splitToList(allowedServices);
    }

    public String authenticate(String authHeader) {
        if (isBlank(authHeader)) {
            throw new UnauthenticatedException("Provided S2S token is missing or invalid");
        } else {
            return authTokenValidator.getServiceName(authHeader);
        }
    }

    public void assertIsAllowedService(String serviceName) {
        if (!allowedServices.contains(serviceName)) {
            throw new ForbiddenException("S2S token is not authorized to use the service");
        }
    }
}
