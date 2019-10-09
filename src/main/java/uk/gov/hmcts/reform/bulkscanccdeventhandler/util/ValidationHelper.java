package uk.gov.hmcts.reform.bulkscanccdeventhandler.util;

import org.apache.commons.validator.routines.EmailValidator;

public final class ValidationHelper {
    private ValidationHelper() {
        // util class
    }

    public static boolean isValidEmailAddress(String email) {
        return EmailValidator.getInstance().isValid(email);
    }
}
