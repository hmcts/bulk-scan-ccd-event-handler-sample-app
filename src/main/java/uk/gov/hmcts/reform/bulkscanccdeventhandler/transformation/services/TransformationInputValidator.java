package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.utils.OcrFieldExtractor;

import java.util.List;
import java.util.Set;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static uk.gov.hmcts.reform.bulkscanccdeventhandler.util.ValidationHelper.isValidEmailAddress;

@Component
public class TransformationInputValidator {

    public void assertIsValid(TransformationInput transformationInput) {

        Set<String> missingFields =
            Sets.difference(
                OcrFieldNames.getRequiredFields(),
                transformationInput.ocrDataFields.stream().map(it -> it.name).collect(toSet())
            );

        if (!missingFields.isEmpty()) {
            throw new InvalidExceptionRecordException(
                missingFields.stream().map(it -> "'" + it + "' is required").collect(toList())
            );
        }
    }

    public List<String> getWarnings(TransformationInput transformationInput) {
        String email = OcrFieldExtractor.get(transformationInput.ocrDataFields, "email");
        return Strings.isNullOrEmpty(email)
            ? singletonList("'email' is empty")
            : getEmailValidationResult(email);
    }

    private List<String> getEmailValidationResult(String email) {
        return isValidEmailAddress(email)
            ? emptyList()
            : singletonList("invalid email '" + email + "'");
    }

}
