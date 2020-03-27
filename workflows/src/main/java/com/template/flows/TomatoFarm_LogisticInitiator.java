package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoHarvestContract;
import com.template.contracts.TomatoLogisticContract;
import com.template.states.FarmBatchDetailsState;
import com.template.states.LogisticBatchDetailsState;
import com.template.states.TomatoFarmState;
import com.template.states.TomatoLogisticState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.crypto.SecureHash;
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
public class TomatoFarm_LogisticInitiator extends FlowLogic<SignedTransaction> {

    private String logisticId;
    private String wayBillNo;
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;
    private Party  carrier;

    public TomatoFarm_LogisticInitiator(String logisticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo, Party carrier) {
        this.logisticId = logisticId;
        this.wayBillNo = wayBillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo = batchNo;
        this.carrier = carrier;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {

        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=getServiceHub().getVaultService().queryBy(TomatoFarmState.class).getStates();
        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
                .stream().filter(farmStateAndRef-> {
                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
                    getLogger().info("Akshay +++"+farmState.getBatchNo());
                    return (farmState.getBatchNo().equals(batchNo));
                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));

        List<StateAndRef<FarmBatchDetailsState>> batchFarmStateAndRefs=getServiceHub().getVaultService().queryBy(FarmBatchDetailsState.class).getStates();
        StateAndRef<FarmBatchDetailsState> inputBatchFarmStateAndRef = batchFarmStateAndRefs
                .stream().filter(farmStateAndRef-> {
                    FarmBatchDetailsState farmState = farmStateAndRef.getState().getData();
                    getLogger().info("Akshay +++"+farmState.getBatchNo());
                    return (farmState.getBatchNo().equals(batchNo));
                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch details was not found."));

        // We choose our transaction's notary (the notary prevents double-spends).
        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        // We get a reference to our own identity.
        Party issuer = getOurIdentity();

        // We create our new LogisticState.
        TomatoLogisticState logisticState=new TomatoLogisticState(new AnonymousParty(carrier.getOwningKey()),logisticId,wayBillNo,pickupDate,pickupLocation,tempInTransit,dropDate,dropLocation,batchNo);

        FarmBatchDetailsState details=inputBatchFarmStateAndRef.getState().getData();
        LogisticBatchDetailsState detailsState=new LogisticBatchDetailsState(details,carrier,logisticId,
                wayBillNo,pickupDate,pickupLocation,tempInTransit,dropDate,dropLocation,batchNo);

        // We build our transaction.
        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        transactionBuilder.addInputState(inputFarmStateAndRef);
        transactionBuilder.addInputState(inputBatchFarmStateAndRef);
        transactionBuilder.addOutputState(logisticState,TomatoLogisticContract.ID);
        transactionBuilder.addOutputState(detailsState,TomatoLogisticContract.ID);

        TomatoLogisticContract.Commands.Transfer_Farm_To_Logistic command=new TomatoLogisticContract.Commands.Transfer_Farm_To_Logistic();

        TomatoHarvestContract.Commands.Transfer farmCommand=new TomatoHarvestContract.Commands.Transfer();

        //why we have to add the assigning party signature with whom we are starting the session
        transactionBuilder.addCommand(command, ImmutableList.of(issuer.getOwningKey(),carrier.getOwningKey()));
        transactionBuilder.addCommand(farmCommand,ImmutableList.of(issuer.getOwningKey(),carrier.getOwningKey()));

        // We check our transaction is valid based on its contracts.
        transactionBuilder.verify(getServiceHub());

        FlowSession session = initiateFlow(carrier);

        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder);

        // The counterparty signs the transaction
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));

    }
}
