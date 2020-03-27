package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import net.corda.core.flows.FlowException;
import net.corda.core.flows.FlowLogic;
import net.corda.core.flows.StartableByRPC;
import net.corda.core.flows.StartableByService;
import net.corda.core.identity.PartyAndCertificate;

import java.util.UUID;

@StartableByRPC
@StartableByService
public class NewKeyForAccount extends FlowLogic<PartyAndCertificate> {

    private UUID accountId;

    public NewKeyForAccount(UUID accountId) {
        this.accountId = accountId;
    }

    @Suspendable
    public PartyAndCertificate call() throws FlowException {
        return getServiceHub().getKeyManagementService().freshKeyAndCert(
                getOurIdentityAndCert(),
                false,
                accountId
        );
    }
}
