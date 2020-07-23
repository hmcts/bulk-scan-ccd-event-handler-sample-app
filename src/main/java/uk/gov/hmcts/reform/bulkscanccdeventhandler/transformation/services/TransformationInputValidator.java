package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import com.google.common.collect.Sets;
import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.model.in.TransformationInput;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class TransformationInputValidator {

    public List<String> getErrors(TransformationInput transformationInput) {

        Set<String> missingFields =
            Sets.difference(
                OcrFieldNames.getRequiredFields(),
                transformationInput.ocrDataFields.stream().map(it -> it.name).collect(toSet())
            );

        return missingFields.stream().map(it -> "'" + it + "' is required").collect(toList());
    }
}
