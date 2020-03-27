package com.template.contracts;

import com.template.states.TomatoDistributorState;
import com.template.states.TomatoLogisticState;
import com.template.states.TomatoRestaurantState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class TomatoDistributorContract implements Contract {

    public static final String ID = "com.template.contracts.TomatoDistributorContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<CommandData> cmd=tx.getCommands().get(0);
        if(cmd.getValue() instanceof Commands.Transfer_Logistic_To_Distrbutor){
            if(tx.getInputStates().size() !=2)throw new IllegalArgumentException("single input state required");
            if(tx.getOutputStates().size() !=2) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
            if(tx.getCommands().size() !=2)throw new IllegalArgumentException("Only single type of transaction should happen in every transaction");
            ContractState outputState=tx.getOutput(0);
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(1);
            if(!(outputState instanceof TomatoDistributorState))throw new IllegalArgumentException("output must be a TomatoRestaurantState");
            if(!(inputState instanceof TomatoLogisticState))throw new IllegalArgumentException("Input must be of TomatoDistributorState ");
            TomatoLogisticState inputLogisticState=(TomatoLogisticState) inputState;
            TomatoDistributorState outputDistributorState=(TomatoDistributorState) outputState;
            List<PublicKey> requiredSigners=commandI.getSigners();
            if(!(requiredSigners.contains(inputLogisticState.getCarrier().getOwningKey()) && requiredSigners.contains(outputDistributorState.getDistributor().getOwningKey())))throw new IllegalArgumentException("Tomato distributor should be a signer to this transaction");


        }
        else if(cmd.getValue() instanceof Commands.Transfer_Distributor_To_Restaurant){
            if(tx.getInputStates().size() !=1)throw new IllegalArgumentException("single input state required");
            if(tx.getOutputStates().size() !=1) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
            if(tx.getCommands().size() !=2)throw new IllegalArgumentException("Only single type of transaction should happen in every transaction");
            ContractState outputState=tx.getOutput(0);
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(0);
            if(!(outputState instanceof TomatoRestaurantState))throw new IllegalArgumentException("output must be a TomatoRestaurantState");
            if(!(inputState instanceof TomatoDistributorState))throw new IllegalArgumentException("Input must be of TomatoDistributorState ");
            TomatoDistributorState inputDistributorState=(TomatoDistributorState) inputState;
            TomatoRestaurantState outputRestaurantState=(TomatoRestaurantState) outputState;
            List<PublicKey> requiredSigners=commandI.getSigners();
            if(!(requiredSigners.contains(inputDistributorState.getDistributor().getOwningKey()) && requiredSigners.contains(
                    outputRestaurantState.getRestaurant().getOwningKey())))throw new IllegalArgumentException("Both tomato distributor and restaurant owner  should be a signer to this transaction");

        }
    }

    public interface Commands extends CommandData{
        class Transfer_Logistic_To_Distrbutor implements Commands{}
        class Transfer_Distributor_To_Restaurant implements Commands{}
    }
}
