package com.template.flows;

import co.paralleluniverse.fibers.Suspendable;
import com.google.common.collect.ImmutableList;
import com.template.contracts.TomatoDistributorContract;
import com.template.contracts.TomatoRestaurantContract;
import com.template.states.DistributorBatchDetailsState;
import com.template.states.RestaurantBatchDetailsState;
import com.template.states.TomatoDistributorState;
import com.template.states.TomatoRestaurantState;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.contracts.StateRef;
import net.corda.core.contracts.TransactionState;
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
public class TomatoDistributor_RestaurantInitiator extends FlowLogic<SignedTransaction> {

    private String restaurantId;
    private String name;
    private String wareHouseInfo;
    private String purchaseOrder;
    private LocalDate receivedDate;
    private String batchNo;
    private Party restaurant;

    public TomatoDistributor_RestaurantInitiator(String restaurantId, String name, String wareHouseInfo, String purchaseOrder, LocalDate receivedDate, String batchNo, Party restaurant) {
        this.restaurantId = restaurantId;
        this.name = name;
        this.wareHouseInfo = wareHouseInfo;
        this.purchaseOrder = purchaseOrder;
        this.receivedDate = receivedDate;
        this.batchNo = batchNo;
        this.restaurant = restaurant;
    }

    @Override
    @Suspendable
    public SignedTransaction call() throws FlowException {
        List<StateAndRef<TomatoDistributorState>> inputStateAndRefs=getServiceHub().getVaultService().queryBy(TomatoDistributorState.class).getStates();

        StateAndRef<TomatoDistributorState>  inputStateAndRef=inputStateAndRefs.stream().filter(
                stateAndRef->{
                    TomatoDistributorState distributorState=stateAndRef.getState().getData();
                    return (distributorState.getBatchNo().equals(batchNo));
                }
        ).findAny().orElseThrow(()->new IllegalArgumentException("The batch with batch no :"+batchNo+" is not available"));

        List<StateAndRef<DistributorBatchDetailsState>> inputDistributorStateAndRefs=getServiceHub().getVaultService().queryBy(DistributorBatchDetailsState.class).getStates();

        StateAndRef<DistributorBatchDetailsState>  inputDistributorStateAndRef=inputDistributorStateAndRefs.stream().filter(
                stateAndRef->{
                    DistributorBatchDetailsState distributorState=stateAndRef.getState().getData();
                    return (distributorState.getBatchNo().equals(batchNo));
                }
        ).findAny().orElseThrow(()->new IllegalArgumentException("The batch details with batch no :"+batchNo+" is not available"));

        Party issuer=getOurIdentity();
        Party notary=getServiceHub().getNetworkMapCache().getNotaryIdentities().get(0);

        TransactionBuilder tx=new TransactionBuilder(notary);
        TomatoRestaurantState restaurantState=new TomatoRestaurantState(new AnonymousParty(restaurant.getOwningKey()),restaurantId,name,wareHouseInfo,purchaseOrder,receivedDate,batchNo);

        DistributorBatchDetailsState distributorBatchDetailsState=inputDistributorStateAndRef.getState().getData();
        RestaurantBatchDetailsState restaurantBatchDetailsState=new RestaurantBatchDetailsState(distributorBatchDetailsState,restaurant,
                restaurantId,name,wareHouseInfo,purchaseOrder,receivedDate,batchNo);
        tx.addInputState(inputStateAndRef);
        tx.addInputState(inputDistributorStateAndRef);

        // first need to check with others whether it is fine or not
//        TomatoDistributorState temp=inputStateAndRef.getState().getData();
//        temp.setCustomer(restaurant);
//        TransactionState<TomatoDistributorState> tempTransaction=new TransactionState<TomatoDistributorState>(temp,getOurIdentity());
//        StateAndRef<TomatoDistributorState> tempState=new StateAndRef<TomatoDistributorState>(tempTransaction,inputStateAndRef.getRef());
//        tx.addInputState(tempState);
        tx.addOutputState(restaurantState, TomatoRestaurantContract.ID);
        tx.addOutputState(restaurantBatchDetailsState,TomatoRestaurantContract.ID);

        TomatoDistributorContract.Commands.Transfer_Distributor_To_Restaurant command=new TomatoDistributorContract.Commands.Transfer_Distributor_To_Restaurant();

        TomatoRestaurantContract.Commands.Transfer restaurantCommand=new TomatoRestaurantContract.Commands.Transfer();

        tx.addCommand(command, ImmutableList.of(issuer.getOwningKey(),restaurant.getOwningKey()));

        tx.addCommand(restaurantCommand,ImmutableList.of(issuer.getOwningKey(),restaurant.getOwningKey()));

        tx.verify(getServiceHub());

        FlowSession session = initiateFlow(restaurant);

        // We sign the transaction with our private key, making it immutable.
        SignedTransaction signedTransaction = getServiceHub().signInitialTransaction(tx);

        // The counterparty signs the transaction
        SignedTransaction fullySignedTransaction = subFlow(new CollectSignaturesFlow(signedTransaction, singletonList(session)));

        // We get the transaction notarised and recorded automatically by the platform.
        return subFlow(new FinalityFlow(fullySignedTransaction, singletonList(session)));

    }
}
