package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import net.corda.core.flows.*;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.WireTransaction;

@InitiatedBy(TomatoDistributor_RestaurantInitiator.class)
public class TomatoDistributor_RestaurantResponder extends FlowLogic<Void> {
    private FlowSession counterpartySession;

    public TomatoDistributor_RestaurantResponder(FlowSession counterpartySession) {
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
