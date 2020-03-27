package com.template.flows.accounts;

import co.paralleluniverse.fibers.Suspendable;
import com.r3.corda.lib.accounts.contracts.states.AccountInfo;
import com.r3.corda.lib.accounts.workflows.flows.CreateAccount;
import net.corda.core.contracts.StateAndRef;
import net.corda.core.flows.*;
import com.r3.corda.lib.accounts.workflows.services.*;


import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@StartableByRPC
@StartableByService
@InitiatingFlow
public class CreateNewAccount extends FlowLogic<String> {


    private String acctName;

    public CreateNewAccount(String acctName) {
        this.acctName = acctName;
    }

    @Suspendable
    public String call() throws FlowException {

        StateAndRef<AccountInfo>  newAccount=null;
        try {
            newAccount = (StateAndRef<AccountInfo>) subFlow(new CreateAccount(acctName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        AccountInfo acct = newAccount.getState().getData();
        return ""+acct.getName() + " team's account was created. UUID is : " + acct.getIdentifier();


    }
}
