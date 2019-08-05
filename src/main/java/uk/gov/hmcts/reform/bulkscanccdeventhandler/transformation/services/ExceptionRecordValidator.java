package uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.services;

import org.springframework.stereotype.Component;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.common.OcrFieldNames;
import uk.gov.hmcts.reform.bulkscanccdeventhandler.transformation.model.in.ExceptionRecord;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class ExceptionRecordValidator {

    public void assertIsValid(ExceptionRecord exceptionRecord) {

        List<String> ocrFieldsInExceptionRecord =
            exceptionRecord
                .ocrDataFields
                .stream()
                .map(it -> it.name)
                .collect(toList());

        List<String> missingFields =
            OcrFieldNames
                .getRequiredFields()
                .stream()
                .filter(reqField -> !ocrFieldsInExceptionRecord.contains(reqField))
                .collect(toList());

        if (!missingFields.isEmpty()) {
            throw new InvalidExceptionRecordException(
                "Missing required fields: " + String.join(", ", missingFields)
            );
        }
    }
}
