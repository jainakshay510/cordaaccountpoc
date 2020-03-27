package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByKey;
import com.template.states.TomatoLogisticState;
import com.template.states.TomatoRestaurantState;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.transactions.SignedTransaction;

import java.util.concurrent.atomic.AtomicReference;

@InitiatedBy(DistributorAccount_RestaurantAccountFlow.class)
@InitiatingFlow
public class DistributorAccount_RestaurantFlowResponder extends FlowLogic<Void> {

    private FlowSession counterpartySession;

    public DistributorAccount_RestaurantFlowResponder(FlowSession counterpartySession) {
        this.counterpartySession = counterpartySession;
    }

    @Suspendable
    public Void call() throws FlowException {

        AtomicReference<AccountInfo> accountInfo=new AtomicReference<AccountInfo>();

        SignedTransaction signedTransaction = subFlow(new SignTransactionFlow(counterpartySession) {
            @Suspendable
            protected void checkTransaction(SignedTransaction stx) throws FlowException {
                AnonymousParty receiverParty = stx.getTx().outRefsOfType(TomatoRestaurantState.class).get(0).getState().getData().getRestaurant();
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
