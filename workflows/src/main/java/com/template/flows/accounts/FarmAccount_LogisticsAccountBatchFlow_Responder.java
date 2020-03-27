package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByKey;
import com.r3.corda.lib.accounts.workflows.flows.RequestAccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.RequestAccountInfoFlow;
import com.template.states.TomatoLogisticState;
import net.corda.core.flows.*;
import net.corda.core.identity.AbstractParty;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;

import java.util.concurrent.atomic.AtomicReference;

@InitiatedBy(FarmAccount_LogisticAccountBatchFlow.class)
@InitiatingFlow
public class FarmAccount_LogisticsAccountBatchFlow_Responder extends FlowLogic<Void> {

    private FlowSession counterpartySession;

    public FarmAccount_LogisticsAccountBatchFlow_Responder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    public Void call() throws FlowException {

        AtomicReference<AccountInfo> accountInfo=new AtomicReference<AccountInfo>();

        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
            @Suspendable
            protected void checkTransaction(SignedTransaction stx) throws FlowException {
                AnonymousParty receiverParty = stx.getTx().outRefsOfType(TomatoLogisticState.class).get(0).getState().getData().getCarrier();
                if(receiverParty != null){
                    accountInfo.set(subFlow(new AccountInfoByKey(receiverParty.getOwningKey())).getState().getData());
                }

                if (accountInfo.get() == null) {
                    throw new IllegalStateException("Account to move to was not found on this node");
                }

            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));

        return null;
    }
}
