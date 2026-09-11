package com.lilamaris.lauth.identity.application.service;

import com.lilamaris.lauth.identity.application.config.CredentialProperties;
import com.lilamaris.lauth.identity.application.internal.event.PasswordResetRequested;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueToken;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenGenerator;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenHasher;
import com.lilamaris.lauth.identity.application.model.opaque.OpaqueTokenPurpose;
import com.lilamaris.lauth.identity.application.port.in.RequestPasswordResetUseCase;
import com.lilamaris.lauth.identity.application.port.in.command.RequestPasswordResetCommand;
import com.lilamaris.lauth.identity.application.port.out.CredentialReader;
import com.lilamaris.lauth.identity.application.port.out.PasswordResetTokenStore;
import com.lilamaris.lauth.identity.domain.PasswordResetToken;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.Clock;

@Service
@Validated
@RequiredArgsConstructor
public class RequestPasswordResetService implements RequestPasswordResetUseCase {
    private final CredentialProperties credentialProperties;
    private final CredentialReader credentialReader;
    private final PasswordResetTokenStore passwordResetTokenStore;
    private final OpaqueTokenGenerator opaqueTokenGenerator;
    private final OpaqueTokenHasher opaqueTokenHasher;
    private final ApplicationEventPublisher eventPublisher;
    private final Clock clock;

    @Transactional
    public void request(RequestPasswordResetCommand command) {
        var context = credentialReader.findContextByEmail(command.email()).orElse(null);
        if (context == null) return;

        var now = clock.instant();
        passwordResetTokenStore.revokeOpenByCredentialId(context.credentialId(), now);

        var token = opaqueTokenGenerator.generate();
        var tokenHash = opaqueTokenHasher.hash(OpaqueTokenPurpose.PASSWORD_RESET_TOKEN, token);

        var expiresAt = now.plus(credentialProperties.passwordResetTokenExpiration());
        var passwordResetToken = PasswordResetToken.of(context.credentialId(), tokenHash, now, expiresAt);
        var passwordResetTokenId = passwordResetTokenStore.save(passwordResetToken).orElse(null);
        if (passwordResetTokenId == null) return;

        var opaqueToken = OpaqueToken.of(passwordResetTokenId.toString(), token);
        eventPublisher.publishEvent(new PasswordResetRequested(command.email(), opaqueToken, expiresAt));

        // 이 OpaqueToken은 해당 요청 응답에 담아서 보내는게 아님. 실제 credential에 기록된 이메일 등으로 발송해서 요청 당사자가 소유한 Credential인지 증명해야함
        // opaqueToken을 쿼리 스트링으로 포함한 특정 API URI을 메일로 발송해야함. 이건 어떻게 해야하지?
        // -> 이메일 서비스를 따로 MSA로 빼고 이벤트 통신?: 이메일 하나 보내자고 인프라 복잡도가 갑자기 높아짐
        // -> 이 서비스 안에서 종결?: 내부 메모리 큐에 저장만 하고 스케쥴 걸어서 이메일 어댑터가 큐에서 꺼내서 발송? JVM 죽으면 정보 다 사라지니까 Outbox를 만들어야하나?
        //    -> 인메모리: JVM 죽으면 재설정 토큰 정보 다 사라짐. 사용자가 해야할 것은? 그냥 다시 재설정 토큰 요청하면 됨.
        //    -> outbox: JVM 죽어도 정보 보존되서 못보낸 이메일 다시 보낼 수 있음. 근데 이러면 outbox에 토큰 해쉬랑 원문(OpaqueToken)이 저장되어있던가 토큰을 해쉬가 아니라 복호화 가능하도록 구조를 바꿔야함.
        // 그럼 이 유스케이스에서 응답해야할 정보는? 토큰 발급 시각 및 만료 시각 정도.
    }
}
