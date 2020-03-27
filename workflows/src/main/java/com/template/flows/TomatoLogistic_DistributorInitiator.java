package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoDistributorContract;
import com.template.contracts.TomatoLogisticContract;
import com.template.states.DistributorBatchDetailsState;
import com.template.states.LogisticBatchDetailsState;
import com.template.states.TomatoDistributorState;
import com.template.states.TomatoLogisticState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;

@InitiatingFlow
@StartableByRPC
public class TomatoLogistic_DistributorInitiator extends FlowLogic<SignedTransaction> {

    private String distributorId;
    private String wayBillNo;
    private LocalDate receivedDate;
    private Double storageTemp;
    private String batchNo;
    private Party receiver;

    public TomatoLogistic_DistributorInitiator(String distributorId, String wayBillNo, LocalDate receivedDate, Double storageTemp, String batchNo, Party receiver) {
        this.distributorId = distributorId;
        this.wayBillNo = wayBillNo;
        this.receivedDate = receivedDate;
        this.storageTemp = storageTemp;
        this.batchNo = batchNo;
        this.receiver = receiver;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        List<StateAndRef<TomatoLogisticState>> inputStateAndRefs=getServiceHub().getVaultService().queryBy(TomatoLogisticState.class).getStates();
        StateAndRef<TomatoLogisticState> inputStateAndRef=inputStateAndRefs.stream().filter(logisticStateAndRef->{
            TomatoLogisticState logisticState=logisticStateAndRef.getState().getData();
            return (logisticState.getBatchNo().equals(batchNo));
        }).findAny().orElseThrow(()->new IllegalArgumentException("No Logistic state present in the vault"));


        List<StateAndRef<LogisticBatchDetailsState>> inputLogisticStateAndRefs=getServiceHub().getVaultService().queryBy(LogisticBatchDetailsState.class).getStates();
        StateAndRef<LogisticBatchDetailsState> inputBatchLogisticStateAndRef=inputLogisticStateAndRefs.stream().filter(logisticStateAndRef->{
            LogisticBatchDetailsState logisticState=logisticStateAndRef.getState().getData();
            return (logisticState.getBatchNo().equals(batchNo));
        }).findAny().orElseThrow(()->new IllegalArgumentException("No Logistic detail state present in the vault"));

        Party notary=getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        Party issuer=getOurIdentity();

        TomatoDistributorState distributorState=new TomatoDistributorState(new AnonymousParty(receiver.getOwningKey()),null,distributorId,wayBillNo,receivedDate,storageTemp,null,null,batchNo);

       LogisticBatchDetailsState logisticBatchDetailsState=inputBatchLogisticStateAndRef.getState().getData();

       DistributorBatchDetailsState distributorBatchDetailsState=new DistributorBatchDetailsState(logisticBatchDetailsState,
                receiver,null,distributorId,wayBillNo,receivedDate,storageTemp,null,null,batchNo);

        TransactionBuilder tx=new TransactionBuilder(notary);

        tx.addInputState(inputStateAndRef);
        tx.addInputState(inputBatchLogisticStateAndRef);

        tx.addOutputState(distributorState, TomatoDistributorContract.ID);
        tx.addOutputState(distributorBatchDetailsState,TomatoDistributorContract.ID);

        TomatoLogisticContract.Commands.Transfer_Logistic_To_Distributor command=new TomatoLogisticContract.Commands.Transfer_Logistic_To_Distributor();

        TomatoDistributorContract.Commands.Transfer_Logistic_To_Distrbutor distributorCommand=new TomatoDistributorContract.Commands.Transfer_Logistic_To_Distrbutor();

        tx.addCommand(command, ImmutableList.of(issuer.getOwningKey(),receiver.getOwningKey()));

        tx.addCommand(distributorCommand,ImmutableList.of(issuer.getOwningKey(),receiver.getOwningKey()));


        tx.verify(getServiceHub());

        FlowSession session = initiateFlow(receiver);

        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(tx);

        // The counterparty signs the transaction
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));


    }
}
