package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.exception.IdentityServiceProgressCode;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenCodec;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenPurpose;
import com.lilamaris.lauth.identity.application.port.in.ResetPasswordUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.ResetPasswordCommand;
import com.lilamaris.lauth.identity.application.port.out.CredentialReader;
import com.lilamaris.lauth.identity.application.port.out.CredentialStore;
import com.lilamaris.lauth.identity.application.port.out.PasswordResetTokenReader;
import com.lilamaris.lauth.identity.application.port.out.PasswordResetTokenStore;
import com.lilamaris.lauth.kernel.application.exception.ApplicationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Clock;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class ResetPasswordService implements ResetPasswordUseCase {
    private final PasswordResetTokenReader passwordResetTokenReader;
    private final PasswordResetTokenStore passwordResetTokenStore;
    private final CredentialStore credentialStore;
    private final OpaqueTokenHasher opaqueTokenHasher;
    private final PasswordEncoder passwordEncoder;
    private final Clock clock;

    @Override
    @Transactional
    public void reset(ResetPasswordCommand command) {
        var now = clock.instant();

        var opaqueToken = OpaqueTokenCodec.decode(command.token());
        var passwordResetTokenId = UUID.fromString(opaqueToken.selector());
        var passwordResetToken = passwordResetTokenReader.findById(passwordResetTokenId)
                .orElseThrow(() -> new ApplicationException(IdentityServiceProgressCode.INVALID_TOKEN));

        if (!passwordResetToken.isAvailable(now))
            throw new ApplicationException(IdentityServiceProgressCode.TOKEN_EXPIRED);

        var matched = opaqueTokenHasher.matches(OpaqueTokenPurpose.PASSWORD_RESET_TOKEN, opaqueToken.value(), passwordResetToken.getTokenHash());
        if (!matched)
            throw new ApplicationException(IdentityServiceProgressCode.INVALID_TOKEN);

        var passwordHash = passwordEncoder.encode(command.password());
        var credentialId = passwordResetToken.getCredentialId();

        var consumed = passwordResetTokenStore.consume(passwordResetTokenId, now);
        if (!consumed)
            throw new ApplicationException(IdentityServiceProgressCode.PASSWORD_RESET_TOKEN_ALREADY_CONSUMED);

        var updated = credentialStore.updatePasswordHash(credentialId, passwordHash, now);
        if (!updated)
            throw new ApplicationException(IdentityServiceProgressCode.PASSWORD_UPDATE_FAILED);
    }
}
