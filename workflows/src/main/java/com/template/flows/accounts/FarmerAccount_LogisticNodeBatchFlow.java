package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
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

@StartableByService
@StartableByRPC
@InitiatingFlow
public class FarmerAccount_LogisticNodeBatchFlow extends FlowLogic<SignedTransaction> {

    private String logisticId;
    private String wayBillNo;
    private LocalDate pickupDate;
    private String pickupLocation;
    private Double tempInTransit;
    private LocalDate dropDate;
    private String dropLocation;
    private String batchNo;
    private Party carrier;
    private String farmAccountName;

    public FarmerAccount_LogisticNodeBatchFlow(String logisticId, String wayBillNo, LocalDate pickupDate, String pickupLocation, Double tempInTransit, LocalDate dropDate, String dropLocation, String batchNo, Party carrier, String farmAccountName) {
        this.logisticId = logisticId;
        this.wayBillNo = wayBillNo;
        this.pickupDate = pickupDate;
        this.pickupLocation = pickupLocation;
        this.tempInTransit = tempInTransit;
        this.dropDate = dropDate;
        this.dropLocation = dropLocation;
        this.batchNo = batchNo;
        this.carrier = carrier;
        this.farmAccountName = farmAccountName;
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

        TomatoLogisticState logisticState=new TomatoLogisticState(new AnonymousParty(carrier.getOwningKey()),
                logisticId,wayBillNo,pickupDate,pickupLocation,tempInTransit,dropDate,dropLocation,batchNo);


        TransactionBuilder transactionBuilder = new TransactionBuilder(notary);
        transactionBuilder.addInputState(inputFarmStateAndRef);
        transactionBuilder.addOutputState(logisticState, TomatoLogisticContract.ID);

        TomatoLogisticContract.Commands.Transfer_Farm_To_Logistic command=new TomatoLogisticContract.Commands.Transfer_Farm_To_Logistic();

        TomatoHarvestContract.Commands.Transfer farmCommand=new TomatoHarvestContract.Commands.Transfer();

        transactionBuilder.addCommand(command, ImmutableList.of(myKey,carrier.getOwningKey()));
        transactionBuilder.addCommand(farmCommand,ImmutableList.of(myKey,carrier.getOwningKey()));


        transactionBuilder.verify(getServiceHub());

        FlowSession session = initiateFlow(carrier);


        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(transactionBuilder,ImmutableList.of(getOurIdentity().getOwningKey(),myKey));

        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));


        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));
    }
}
