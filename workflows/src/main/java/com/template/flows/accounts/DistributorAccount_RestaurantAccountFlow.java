package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.AccountInfoByName;
import com.r3.corda.lib.accounts.workflows.flows.RequestKeyForAccount;
import com.template.contracts.TomatoDistributorContract;
import com.template.contracts.TomatoRestaurantContract;
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
@StartableByRPC
@StartableByService
public class DistributorAccount_RestaurantAccountFlow extends FlowLogic<SignedTransaction> {

    private String distributorAccountName;
    private String targetAccountName;
    private String restaurantId;
    private String name;
    private String wareHouseInfo;
    private String purchaseOrder;
    private LocalDate receivedDate;
    private String batchNo;

    public DistributorAccount_RestaurantAccountFlow(String distributorAccountName, String targetAccountName, String restaurantId, String name, String wareHouseInfo, String purchaseOrder, LocalDate receivedDate, String batchNo) {
        this.distributorAccountName = distributorAccountName;
        this.targetAccountName = targetAccountName;
        this.restaurantId = restaurantId;
        this.name = name;
        this.wareHouseInfo = wareHouseInfo;
        this.purchaseOrder = purchaseOrder;
        this.receivedDate = receivedDate;
        this.batchNo = batchNo;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {

        AccountInfo myaccount=subFlow(new AccountInfoByName(distributorAccountName)).get(0).getState().getData();
        QueryCriteria criteria=new QueryCriteria.VaultQueryCriteria().withExternalIds(ImmutableList.of(myaccount.getIdentifier().getId()));
        List<StateAndRef<TomatoDistributorState>> inputStateAndRefs=getServiceHub().getVaultService().queryBy(TomatoDistributorState.class,criteria).getStates();
        StateAndRef<TomatoDistributorState>  inputStateAndRef=inputStateAndRefs.stream().filter(
                stateAndRef->{
                    TomatoDistributorState distributorState=stateAndRef.getState().getData();
                    return (distributorState.getBatchNo().equals(batchNo));
                }
        ).findAny().orElseThrow(()->new IllegalArgumentException("The batch with batch no :"+batchNo+" is not available"));

//        List<StateAndRef<DistributorBatchDetailsState>> inputDistributorStateAndRefs=getServiceHub().getVaultService().queryBy(DistributorBatchDetailsState.class).getStates();
//
//        StateAndRef<DistributorBatchDetailsState>  inputDistributorStateAndRef=inputDistributorStateAndRefs.stream().filter(
//                stateAndRef->{
//                    DistributorBatchDetailsState distributorState=stateAndRef.getState().getData();
//                    return (distributorState.getBatchNo().equals(batchNo));
//                }
//        ).findAny().orElseThrow(()->new IllegalArgumentException("The batch details with batch no :"+batchNo+" is not available"));

        //Party issuer=getOurIdentity();
        Party notary=getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        PublicKey myKey=subFlow(new NewKeyForAccount(myaccount.getIdentifier().getId())).getOwningKey();

        AccountInfo targetAccount = subFlow(new AccountInfoByName(targetAccountName)).get(0).getState().getData();
        AnonymousParty targetAcctAnonymousParty = subFlow(new RequestKeyForAccount(targetAccount));


        TransactionBuilder tx=new TransactionBuilder(notary);
        TomatoRestaurantState restaurantState=new TomatoRestaurantState(targetAcctAnonymousParty,restaurantId,name,wareHouseInfo,purchaseOrder,receivedDate,batchNo);

//        DistributorBatchDetailsState distributorBatchDetailsState=inputDistributorStateAndRef.getState().getData();
//        RestaurantBatchDetailsState restaurantBatchDetailsState=new RestaurantBatchDetailsState(distributorBatchDetailsState,restaurant,
//                restaurantId,name,wareHouseInfo,purchaseOrder,receivedDate,batchNo);
        tx.addInputState(inputStateAndRef);
        //tx.addInputState(inputDistributorStateAndRef);

        // first need to check with others whether it is fine or not
//        TomatoDistributorState temp=inputStateAndRef.getState().getData();
//        temp.setCustomer(restaurant);
//        TransactionState<TomatoDistributorState> tempTransaction=new TransactionState<TomatoDistributorState>(temp,getOurIdentity());
//        StateAndRef<TomatoDistributorState> tempState=new StateAndRef<TomatoDistributorState>(tempTransaction,inputStateAndRef.getRef());
//        tx.addInputState(tempState);
        tx.addOutputState(restaurantState, TomatoRestaurantContract.ID);
        //tx.addOutputState(restaurantBatchDetailsState,TomatoRestaurantContract.ID);

        TomatoDistributorContract.Commands.Transfer_Distributor_To_Restaurant command=new TomatoDistributorContract.Commands.Transfer_Distributor_To_Restaurant();

        TomatoRestaurantContract.Commands.Transfer restaurantCommand=new TomatoRestaurantContract.Commands.Transfer();

        tx.addCommand(command, ImmutableList.of(myKey,targetAcctAnonymousParty.getOwningKey()));

        tx.addCommand(restaurantCommand,ImmutableList.of(myKey,targetAcctAnonymousParty.getOwningKey()));

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
