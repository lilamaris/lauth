package com.lilamaris.lauth.identity.application.port.out;

import com.lilamaris.lauth.identity.domain.FederatedAccount;

public interface FederatedAccountStore {
    boolean tryCreate(FederatedAccount federatedAccount);
}
