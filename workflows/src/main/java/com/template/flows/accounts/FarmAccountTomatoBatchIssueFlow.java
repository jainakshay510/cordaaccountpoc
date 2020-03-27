package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
import com.r3.corda.lib.accounts.workflows.services.AccountService;
import com.template.contracts.TomatoHarvestContract;
import com.template.states.TomatoFarmState;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.time.LocalDate;


@InitiatingFlow
@StartableByRPC
@StartableByService
public class FarmAccountTomatoBatchIssueFlow extends FlowLogic<SignedTransaction> {

    private String farmAccountName;
    private String farmId;
    private String batchNo;
    private String location;
    private LocalDate harvestDate;
    private Double meanTemp;
    private Double meanNitrogen;
    private String category;

    public FarmAccountTomatoBatchIssueFlow(String farmAccountName, String farmId, String batchNo, String location, LocalDate harvestDate, Double meanTemp, Double meanNitrogen, String category) {
        this.farmAccountName = farmAccountName;
        this.farmId = farmId;
        this.batchNo = batchNo;
        this.location = location;
        this.harvestDate = harvestDate;
        this.meanTemp = meanTemp;
        this.meanNitrogen = meanNitrogen;
        this.category = category;
    }

    @Suspendable
    public SignedTransaction call() throws FlowException {

        Party notary=getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        AccountInfo myaccount=subFlow(new AccountInfoByName(farmAccountName)).get(0).getState().getData();
        PublicKey myKey=subFlow(new NewKeyForAccount(myaccount.getIdentifier().getId())).getOwningKey();


        //generating state for transfer
        TomatoFarmState farmState=new TomatoFarmState(new AnonymousParty(myKey),null,farmId,
                batchNo,location,harvestDate,meanTemp,meanNitrogen,category);

        TomatoHarvestContract.Commands.Issue issueCommand=new TomatoHarvestContract.Commands.Issue();

        TransactionBuilder tx=new TransactionBuilder(notary);
        tx.addOutputState(farmState, TomatoHarvestContract.ID);
        tx.addCommand(issueCommand,myKey);

        tx.verify(getServiceHub());

        SignedTransaction signedTransaction=getServiceHub().signInitialTransaction(tx,myKey);

        return subFlow(new FinalityFlow(signedTransaction, ImmutableList.of()));



    }
}
