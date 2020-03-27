package com.template.contracts;

import com.template.states.TomatoFarmState;
import com.template.states.TomatoLogisticState;
import net.corda.core.contracts.*;
import net.corda.core.transactions.LedgerTransaction;
import org.jetbrains.annotations.NotNull;

import java.security.PublicKey;
import java.util.List;

public class TomatoHarvestContract implements Contract {


    public static final String ID = "com.template.contracts.TomatoHarvestContract";

    @Override
    public void verify(@NotNull LedgerTransaction tx) throws IllegalArgumentException {

            CommandWithParties<CommandData> cmd=tx.getCommands().get(0);
            if(cmd.getValue() instanceof Commands.Transfer){
                if(tx.getInputStates().size() !=2)throw new IllegalArgumentException("Isingle input state required");
                if(tx.getOutputStates().size() !=2) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
                if(tx.getCommands().size() !=1)throw new IllegalArgumentException("Only single typpe of transaction should happen in every transaction");
                ContractState outputState=tx.getOutput(0);
                ContractState inputState=tx.getInput(0);
                Command commandI=tx.getCommand(0);
                if(!(outputState instanceof TomatoLogisticContract))throw new IllegalArgumentException("output must be a TomatoLogisticState");
                if(!(inputState instanceof TomatoHarvestContract))throw new IllegalArgumentException("Input must be of TomatoLogisticContract ");
                TomatoFarmState inputfarmState=(TomatoFarmState)inputState;
                TomatoLogisticState tomatoLogisticState=(TomatoLogisticState)outputState;
                List<PublicKey> requiredSigners=commandI.getSigners();
                if(!(requiredSigners.contains(inputfarmState.getSupplier().getOwningKey()) && requiredSigners.contains(inputfarmState.getSuppliedTo().getOwningKey())))throw new IllegalArgumentException("Tomato supplier should be a signerto this transaction");
                if(!(requiredSigners.contains(tomatoLogisticState.getCarrier().getOwningKey())))throw new IllegalArgumentException("Logistic carrier should be a signing party");

            }
            else if(cmd.getValue() instanceof Commands.Issue){
                if(tx.getInputStates().size() !=0)throw new IllegalArgumentException("Toamto Batch cration so input state required");
                if(tx.getOutputStates().size() !=1) throw new IllegalArgumentException("There should be a single batch of tomatoes in every transaction");
                if(tx.getCommands().size() !=1)throw new IllegalArgumentException("Only single type of transaction should happen in every transaction");
                ContractState outputState=tx.getOutput(0);
                Command commandI=tx.getCommand(0);
                if(!(outputState instanceof TomatoFarmState))throw new IllegalArgumentException("output must be a TomatoLogisticState");
                TomatoFarmState tomatoFarmState=(TomatoFarmState) outputState;
                List<PublicKey> requiredSigners=commandI.getSigners();
                if(!(requiredSigners.contains(tomatoFarmState.getSupplier().getOwningKey())))throw new IllegalArgumentException("Owning Farmer  should be a signing party as batch is created by flow initiator which is this Farmer itself");

            }

    }

    public interface Commands extends CommandData{

        class Issue implements  Commands{

        }
        class Transfer implements Commands{ }

    }
}
