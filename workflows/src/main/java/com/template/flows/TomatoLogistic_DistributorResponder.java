package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;

@InitiatedBy(TomatoLogistic_DistributorInitiator.class)
public class TomatoLogistic_DistributorResponder  extends FlowLogic<Void> {

    private FlowSession counterpartySession;

    public TomatoLogistic_DistributorResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        // TokenIssueResponder flow logic goes here.
        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
            @Suspendable
            protected void checkTransaction(SignedTransaction stx) throws FlowException {
                WireTransaction txb = stx.getTx();

            }
        });
        subFlow(new ReceiveFinalityFlow(counterpartySession, signedTransaction.getId()));
        return null;

    }
}
