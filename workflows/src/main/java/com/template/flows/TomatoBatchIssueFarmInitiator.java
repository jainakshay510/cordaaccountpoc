package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoHarvestContract;
import com.template.states.FarmBatchDetailsState;
import com.template.states.TomatoFarmState;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;
import net.corda.core.utilities.ProgressTracker;

import javax.annotation.Signed;

import java.time.LocalDate;

import static java.util.Collections.singletonList;

// ******************
// * TomatoBatchIssueFarmInitiator flow *
// ******************
@InitiatingFlow
@StartableByRPC
public class TomatoBatchIssueFarmInitiator extends FlowLogic<SignedTransaction> {


    //just for temperory purpose
    private String farmId;
    private String batchNo;
    private String location;
    private LocalDate harvestDate;
    private Double meanTemp;
    private Double meanNitrogen;
    private String category;




    public TomatoBatchIssueFarmInitiator(String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen,String category) {
        this.farmId = farmId;
        this.batchNo = batchNo;
        this.location = location;
        this.harvestDate = harvestDate;
        this.meanTemp = meanTemp;
        this.meanNitrogen = meanNitrogen;
        this.category=category;
    }


    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {

        Party notary = getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        //for temperory purpose

        TomatoFarmState farmState=new TomatoFarmState(new AnonymousParty(getOurIdentity().getOwningKey()),null,farmId,batchNo,location,harvestDate,meanTemp,meanNitrogen,category);

        FarmBatchDetailsState detailsState=new FarmBatchDetailsState(getOurIdentity(),farmId,batchNo,location,harvestDate,meanTemp,meanNitrogen,category);
        TransactionBuilder tx=new TransactionBuilder(notary);
        tx.addOutputState(farmState, TomatoHarvestContract.ID);
        tx.addOutputState(detailsState,TomatoHarvestContract.ID);
        TomatoHarvestContract.Commands.Issue command=new TomatoHarvestContract.Commands.Issue();
        tx.addCommand(command, ImmutableList.of(getOurIdentity().getOwningKey()));

        tx.verify(getServiceHub());


        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(tx);

        return subFlow(new FinalityFlow(signedTransaction, ImmutableList.of()));



    }
}
