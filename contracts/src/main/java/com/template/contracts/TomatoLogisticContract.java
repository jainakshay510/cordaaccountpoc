package com.template.contracts;

import com.template.states.TomatoDistributorState;
import com.template.states.TomatoFarmState;
import com.template.states.TomatoLogisticState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class TomatoLogisticContract implements Contract {
    public static final String ID = "com.template.contracts.TomatoLogisticContract";
    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {
        CommandWithParties<CommandData> cmd=tx.getCommands().get(0);
        if(cmd.getValue() instanceof Commands.Transfer_Farm_To_Logistic){
            if(tx.getInputStates().size() !=1)throw new IllegalArgumentException("single input state required");
            if(tx.getOutputStates().size() !=1) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
            if(tx.getCommands().size() !=2)throw new IllegalArgumentException("Only single type of transaction should happen in every transaction");
            ContractState outputState1=tx.getOutput(0);
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(0);
            if(!(outputState1 instanceof TomatoLogisticState))throw new IllegalArgumentException("output must be a TomatoDistributorState");
            if(!(inputState instanceof TomatoFarmState))throw new IllegalArgumentException("Input must be of TomatoLogisticContract ");
            TomatoFarmState inputFarmState=(TomatoFarmState) inputState;
            TomatoLogisticState ouptutLogisticState=(TomatoLogisticState) outputState1;
            List<PublicKey> requiredSigners=commandI.getSigners();
            System.out.println("Contract Akshay "+requiredSigners.get(0)+requiredSigners.get(1));
            System.out.println("INputFarmstate keys "+inputFarmState.getSupplier().getOwningKey());
            System.out.println("Logistic keys "+ouptutLogisticState.getCarrier().getOwningKey());
           // if(!(requiredSigners.contains(inputFarmState.getSupplier().getOwningKey())))throw new IllegalArgumentException("Tomato carrier should be a signer to this transaction");
            if(!(requiredSigners.contains(ouptutLogisticState.getCarrier().getOwningKey())))throw new IllegalArgumentException("receiver and carrier should be a signing party");

        }
        else if(cmd.getValue() instanceof Commands.Transfer_Logistic_To_Distributor){
            if(tx.getInputStates().size() !=1)throw new IllegalArgumentException("single input state required");
            if(tx.getOutputStates().size() !=1) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
            if(tx.getCommands().size() !=2)throw new IllegalArgumentException("Only single type of transaction should happen in every transaction");
            ContractState outputState1=tx.getOutput(0);
            ContractState inputState=tx.getInput(0);
            Command commandI=tx.getCommand(0);
            if(!(outputState1 instanceof TomatoDistributorState))throw new IllegalArgumentException("output must be a TomatoDistributorState");
            if(!(inputState instanceof TomatoLogisticState))throw new IllegalArgumentException("Input must be of TomatoLogisticContract ");
            TomatoLogisticState inputLogisticState=(TomatoLogisticState) inputState;
            TomatoDistributorState outputDistributorState=(TomatoDistributorState) outputState1;
            List<PublicKey> requiredSigners=commandI.getSigners();
            System.out.println(requiredSigners.get(0)+"----------------"+requiredSigners.get(1));
            System.out.println(inputLogisticState.getCarrier().getOwningKey());

           // if(!(requiredSigners.contains(inputLogisticState.getCarrier().getOwningKey())))throw new IllegalArgumentException("Tomato carrier should be a signer to this transaction");
            if(!(requiredSigners.contains(outputDistributorState.getDistributor().getOwningKey())))throw new IllegalArgumentException("receiver and carrier should be a signing party");

        }
    }

    public interface Commands extends CommandData{
        class Transfer_Farm_To_Logistic implements Commands{}
        class Transfer_Logistic_To_Distributor implements Commands{}
    }
}
