package com.template.contracts;

import com.template.states.TomatoDistributorState;
import com.template.states.TomatoLogisticState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class TomatoRestaurantContract implements Contract {

    public static final String ID = "com.template.contracts.TomatoRestaurantContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<CommandData> cmd=tx.getCommands().get(0);
        if( cmd.getValue() instanceof Commands.Transfer){
            if(tx.getInputStates().size() !=2)throw new IllegalArgumentException("single input state required");
            if(tx.getOutputStates().size() !=2) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
            if(tx.getCommands().size() !=1)throw new IllegalArgumentException("Only single typpe of transaction should happen in every transaction");
            ContractState outputState1=tx.getOutput(0);
            ContractState outputState2=tx.getOutput(1);
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(0);
            if(!(outputState1 instanceof TomatoDistributorState))throw new IllegalArgumentException("output must be a TomatoDistributorState");
            if(!(inputState instanceof TomatoLogisticState))throw new IllegalArgumentException("Input must be of TomatoLogisticContract ");
            TomatoLogisticState inputLogisticState=(TomatoLogisticState) inputState;
            TomatoDistributorState outputDistributorState=(TomatoDistributorState) outputState1;
            List<PublicKey> requiredSigners=commandI.getSigners();
            if(!(requiredSigners.contains(inputLogisticState.getCarrier().getOwningKey())))throw new IllegalArgumentException("Tomato carrier should be a signer to this transaction");
            if(!(requiredSigners.contains(outputDistributorState.getDistributor().getOwningKey())))throw new IllegalArgumentException("receiver and carrier should be a signing party");

        }

    }

    public interface Commands extends CommandData{
        class Transfer implements Commands{}
    }
}
