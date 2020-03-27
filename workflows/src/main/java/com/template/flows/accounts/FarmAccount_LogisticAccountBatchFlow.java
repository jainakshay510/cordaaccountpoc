package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.template.contracts.TomatoHarvestContract;
import com.template.contracts.TomatoLogisticContract;
import com.template.states.TomatoFarmState;
import com.template.states.TomatoLogisticState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import net.corda.core.identity.AnonymousParty;
import net.corda.core.identity.Party;
import net.corda.core.node.services.vault.QueryCriteria;
import net.corda.core.transactions.SignedTransaction;
import net.corda.core.transactions.TransactionBuilder;

import java.security.PublicKey;
import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.singletonList;

@InitiatingFlow
@StartableByRPC
@StartableByService
public class FarmAccount_LogisticAccountBatchFlow extends FlowLogic<SignedTransaction> {

    private String farmAccountName;
    private String targetAccountName;
    private String logisticId;
    private String wayBillNo;
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;


    public FarmAccount_LogisticAccountBatchFlow(String farmAccount, String tragetAccount, String logisticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo) {
        this.farmAccountName = farmAccount;
        this.targetAccountName = tragetAccount;
        this.logisticId = logisticId;
        this.wayBillNo = wayBillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo = batchNo;

    }


    @Suspendable
    public SignedTransaction call() throws FlowException {
        Party notary=getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);
        AccountInfo myaccount=subFlow(new AccountInfoByName(farmAccountName)).get(0).getState().getData();
        QueryCriteria criteria=new QueryCriteria.VaultQueryCriteria().withExternalIds(ImmutableList.of(myaccount.getIdentifier().getId()));
        List<StateAndRef<TomatoFarmState>> farmStateAndRefs=getServiceHub().getVaultService().queryBy(TomatoFarmState.class,criteria).getStates();
        StateAndRef<TomatoFarmState> inputFarmStateAndRef = farmStateAndRefs
                .stream().filter(farmStateAndRef-> {
                    TomatoFarmState farmState = farmStateAndRef.getState().getData();
                    getLogger().info("Akshay +++"+farmState.getBatchNo());
                    return (farmState.getBatchNo().equals(batchNo));
                }).findAny().orElseThrow(() -> new IllegalArgumentException("The batch was not found."));


        PublicKey myKey=subFlow(new NewKeyForAccount(myaccount.getIdentifier().getId())).getOwningKey();

        AccountInfo targetAccount = subFlow(new AccountInfoByName(targetAccountName)).get(0).getState().getData();
        AnonymousParty targetAcctAnonymousParty = subFlow(new RequestKeyForAccount(targetAccount));

        TomatoLogisticState logisticState=new TomatoLogisticState(targetAcctAnonymousParty,
                logisticId,wayBillNo,pickupDate,pickupLocation,tempInTransit,dropDate,dropLocation,batchNo);


        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        transactionBuilder.addInputState(inputFarmStateAndRef);
        transactionBuilder.addOutputState(logisticState, TomatoLogisticContract.ID);

        TomatoLogisticContract.Commands.Transfer_Farm_To_Logistic command=new TomatoLogisticContract.Commands.Transfer_Farm_To_Logistic();

        TomatoHarvestContract.Commands.Transfer farmCommand=new TomatoHarvestContract.Commands.Transfer();

        transactionBuilder.addCommand(command, ImmutableList.of(myKey,targetAcctAnonymousParty.getOwningKey()));
        transactionBuilder.addCommand(farmCommand,ImmutableList.of(myKey,targetAcctAnonymousParty.getOwningKey()));


        transactionBuilder.verify(getServiceHub());

        FlowSession session = initiateFlow(targetAccount.getHost());


        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder,ImmutableList.of(getOurIdentity().getOwningKey(),myKey));

        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));


        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
    }

}
