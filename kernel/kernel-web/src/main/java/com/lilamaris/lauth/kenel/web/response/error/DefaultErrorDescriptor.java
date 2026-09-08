package com.lilamaris.lauth.kenel.web.response.error;

import com.lilamaris.cozyr.kernel.core.condition.ObjectPrecondition;
import com.lilamaris.cozyr.kernel.core.condition.StringPrecondition;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationCode;
import com.lilamaris.shrturl.kernel.application.exception.ApplicationProgressCode;
import com.lilamaris.shrturl.kernel.application.exception.ProcessReason;
import com.lilamaris.shrturl.kernel.application.exception.ProgressType;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public record DefaultErrorDescriptor(
        ProcessReason reason,
        @Nullable String resource,
        ProgressType type,
        String message
) implements ErrorDescriptor {
    public DefaultErrorDescriptor {
        ObjectPrecondition.requireNonNull(reason, "reason");
        if (resource != null) StringPrecondition.requireNonBlank(resource, "resource");
        ObjectPrecondition.requireNonNull(type, "type");
        StringPrecondition.requireNonBlank(message, "message");
    }

    public DefaultErrorDescriptor(ProcessReason reason, ProgressType type, String message) {
        this(reason, null, type, message);
    }

    public static DefaultErrorDescriptor of(ProcessReason reason, ProgressType type, String message) {
        return new DefaultErrorDescriptor(reason, type, message);
    }

    public static DefaultErrorDescriptor of(ProcessReason reason, String resource, ProgressType type, String message) {
        return new DefaultErrorDescriptor(reason, resource, type, message);
    }

    public static DefaultErrorDescriptor from(ApplicationCode applicationCode) {
        ObjectPrecondition.requireNonNull(applicationCode, "applicationCode");
        if (applicationCode instanceof ApplicationProgressCode progressCode) {
            return new DefaultErrorDescriptor(
                    progressCode.reason(),
                    progressCode.resourceName(),
                    progressCode.type(),
                    progressCode.message()
            );
        }

        return new DefaultErrorDescriptor(
                applicationCode.reason(),
                applicationCode.type(),
                applicationCode.message()
        );
    }

    @Override
    public Optional<String> resourceName() {
        return Optional.ofNullable(resource);
    }
}
