package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;

// ******************
// * TomatoBatchIssueFarmResponder flow *
// ******************
@InitiatedBy(TomatoBatchIssueFarmInitiator.class)
public class TomatoBatchIssueFarmResponder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public TomatoBatchIssueFarmResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    @Override
    public Void call() throws FlowException {
        // TomatoBatchIssueFarmResponder flow logic goes here.
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
