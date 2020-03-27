package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.template.contracts.TomatoDistributorContract;
import com.template.contracts.TomatoLogisticContract;
import com.template.states.*;
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
@StartableByService
@StartableByRPC
public class LogisticAccount_DistributorAccountBatchFlow extends FlowLogic<SignedTransaction> {

    private String logisticAccountName;
    private String targetAccountName;
    private String distributorId;
    private String wayBillNo;
    private LocalDate receivedDate;
    private Double storageTemp;
    private String batchNo;


    public LogisticAccount_DistributorAccountBatchFlow(String logisticAccountName, String targetAccountName, String distributorId, String wayBillNo, LocalDate receivedDate, Double storageTemp, String batchNo) {
        this.logisticAccountName = logisticAccountName;
        this.targetAccountName = targetAccountName;
        this.distributorId = distributorId;
        this.wayBillNo = wayBillNo;
        this.receivedDate = receivedDate;
        this.storageTemp = storageTemp;
        this.batchNo = batchNo;

    }

    @Suspendable
    public SignedTransaction call() throws FlowException {

        AccountInfo myaccount=subFlow(new AccountInfoByName(logisticAccountName)).get(0).getState().getData();
        QueryCriteria criteria=new QueryCriteria.VaultQueryCriteria().withExternalIds(ImmutableList.of(myaccount.getIdentifier().getId()));
        List<StateAndRef<TomatoLogisticState>> inputStateAndRefs=getServiceHub().getVaultService().queryBy(TomatoLogisticState.class,criteria).getStates();
        StateAndRef<TomatoLogisticState> inputStateAndRef=inputStateAndRefs.stream().filter(logisticStateAndRef->{
            TomatoLogisticState logisticState=logisticStateAndRef.getState().getData();
            return (logisticState.getBatchNo().equals(batchNo));
        }).findAny().orElseThrow(()->new IllegalArgumentException("No Logistic state present in the vault"));


//        List<StateAndRef<LogisticBatchDetailsState>> inputLogisticStateAndRefs=getServiceHub().getVaultService().queryBy(LogisticBatchDetailsState.class).getStates();
//        StateAndRef<LogisticBatchDetailsState> inputBatchLogisticStateAndRef=inputLogisticStateAndRefs.stream().filter(logisticStateAndRef->{
//            LogisticBatchDetailsState logisticState=logisticStateAndRef.getState().getData();
//            return (logisticState.getBatchNo().equals(batchNo));
//        }).findAny().orElseThrow(()->new IllegalArgumentException("No Logistic detail state present in the vault"));

        Party notary=getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        PublicKey myKey=subFlow(new NewKeyForAccount(myaccount.getIdentifier().getId())).getOwningKey();

        AccountInfo targetAccount = subFlow(new AccountInfoByName(targetAccountName)).get(0).getState().getData();
        AnonymousParty targetAcctAnonymousParty = subFlow(new RequestKeyForAccount(targetAccount));


        TomatoDistributorState distributorState=new TomatoDistributorState(targetAcctAnonymousParty,null,distributorId,wayBillNo,receivedDate,storageTemp,null,null,batchNo);

        //LogisticBatchDetailsState logisticBatchDetailsState=inputBatchLogisticStateAndRef.getState().getData();

//        DistributorBatchDetailsState distributorBatchDetailsState=new DistributorBatchDetailsState(logisticBatchDetailsState,
//                receiver,null,distributorId,wayBillNo,receivedDate,storageTemp,null,null,batchNo);

        TransactionBuilder tx=new TransactionBuilder(notary);

        tx.addInputState(inputStateAndRef);
        //tx.addInputState(inputBatchLogisticStateAndRef);

        tx.addOutputState(distributorState, TomatoDistributorContract.ID);
      //  tx.addOutputState(distributorBatchDetailsState,TomatoDistributorContract.ID);

        TomatoLogisticContract.Commands.Transfer_Logistic_To_Distributor command=new TomatoLogisticContract.Commands.Transfer_Logistic_To_Distributor();

        TomatoDistributorContract.Commands.Transfer_Logistic_To_Distrbutor distributorCommand=new TomatoDistributorContract.Commands.Transfer_Logistic_To_Distrbutor();

        tx.addCommand(command, ImmutableList.of(myKey,targetAcctAnonymousParty.getOwningKey()));

        tx.addCommand(distributorCommand,ImmutableList.of(myKey,targetAcctAnonymousParty.getOwningKey()));


        tx.verify(getServiceHub());

        FlowSession session = initiateFlow(targetAccount.getHost());

        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(tx,ImmutableList.of(getOurIdentity().getOwningKey(),myKey));

        // The counterparty signs the transaction
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));


    }

}
