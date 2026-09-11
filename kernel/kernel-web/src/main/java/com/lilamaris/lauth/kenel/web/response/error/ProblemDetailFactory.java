package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.lauth.kernel.application.exception.ApplicationCode;
import com.lilamaris.lauth.kernel.application.exception.ProcessReason;
import com.lilamaris.lauth.kernel.application.exception.ProgressType;
import com.lilamaris.lauth.kernel.core.condition.ObjectPrecondition;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;

import java.util.Locale;
import java.util.Map;

@RequiredArgsConstructor
public class ProblemDetailFactory {
    private final ErrorCodeResolver errorCodeResolver;
    private final TypeUriResolver typeUriResolver;
    private final ErrorStatusRegistry errorStatusRegistry;

    public ProblemDetail from(ApplicationCode applicationCode) {
        return from(DefaultErrorDescriptor.from(applicationCode));
    }

    public ProblemDetail from(ProcessReason reason, ProgressType type, String message) {
        return from(DefaultErrorDescriptor.of(reason, type, message));
    }

    public ProblemDetail from(ProcessReason reason, String resourceName, ProgressType type, String message) {
        return from(DefaultErrorDescriptor.of(reason, resourceName, type, message));
    }

    public ProblemDetail from(ErrorDescriptor descriptor) {
        ObjectPrecondition.requireNonNull(descriptor, "descriptor");

        var type = descriptor.type().canonicalName();
        var message = descriptor.message();

        var httpStatus = errorStatusRegistry.get(type);
        var typeUri = typeUriResolver.resolve(descriptor);
        var code = errorCodeResolver.resolve(descriptor);
        var title = type.toUpperCase(Locale.ROOT);

        var problem = ProblemDetail.forStatusAndDetail(httpStatus, message);

        problem.setTitle(title);
        problem.setType(typeUri);
        problem.setProperty("code", code);

        return problem;
    }

    public ProblemDetail from(ErrorDescriptor descriptor, Map<String, Object> additionalProperties) {
        var problem = from(descriptor);
        additionalProperties.forEach(problem::setProperty);

        return problem;
    }
}
