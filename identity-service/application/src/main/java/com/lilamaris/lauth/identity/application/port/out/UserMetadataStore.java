package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.application.port.out.status.UpdateDisplayNameStatus;
import com.lilamaris.lauth.identity.application.port.out.status.UpdateHandleStatus;

import java.time.Instant;
import java.util.UUID;

public interface UserMetadataStore {
    UpdateHandleStatus updateHandle(UUID userId, String handle, Instant updatedAt);

    UpdateDisplayNameStatus updateDisplayName(UUID userId, String displayName, Instant updatedAt);
}
